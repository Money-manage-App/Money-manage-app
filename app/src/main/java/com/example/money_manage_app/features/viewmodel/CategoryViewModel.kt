package com.example.money_manage_app.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money_manage_app.data.local.DefaultCategories
import com.example.money_manage_app.data.local.entity.CategoryEntity
import com.example.money_manage_app.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    private val _expenseCategories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val expenseCategories: StateFlow<List<CategoryEntity>> = _expenseCategories

    private val _incomeCategories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val incomeCategories: StateFlow<List<CategoryEntity>> = _incomeCategories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val loadMutex = Mutex()
    private var currentUserId: String = ""

    fun setUserId(userId: String) {
        if (currentUserId != userId && userId.isNotEmpty()) {
            currentUserId = userId
            loadCategories()
        }
    }

    fun loadCategories() {
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            loadMutex.withLock {
                if (_isLoading.value) return@withLock

                _isLoading.value = true
                _error.value = null

                try {
                    // ✅ Fix categories with ID = 0 FIRST
                    repository.fixCategoriesWithZeroId(currentUserId)

                    // ✅ CRITICAL: Always reload from database after fix
                    // This ensures UI has the correct IDs
                    val totalCount = repository.countCategories(currentUserId)

                    if (totalCount == 0) {
                        // New user → Create default categories
                        val defaultCategories = DefaultCategories.createDefaultCategoriesForUser(currentUserId)
                        repository.insertAll(defaultCategories)
                    }

                    // ✅ ALWAYS load fresh data from database
                    // Never use cached or old category objects
                    _expenseCategories.value = repository.getExpenseCategories(currentUserId)
                    _incomeCategories.value = repository.getIncomeCategories(currentUserId)

                    // ✅ DEBUG: Log category IDs to verify they're correct
                    android.util.Log.d("CategoryViewModel", "Loaded ${_expenseCategories.value.size} expense categories")
                    _expenseCategories.value.forEach {
                        android.util.Log.d("CategoryViewModel", "  - ${it.nameVi}: ID=${it.id}")
                    }
                    android.util.Log.d("CategoryViewModel", "Loaded ${_incomeCategories.value.size} income categories")
                    _incomeCategories.value.forEach {
                        android.util.Log.d("CategoryViewModel", "  - ${it.nameVi}: ID=${it.id}")
                    }

                } catch (e: Exception) {
                    _error.value = "Không thể tải danh mục: ${e.message}"
                    android.util.Log.e("CategoryViewModel", "Error loading categories", e)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun addCategory(name: String, iconName: String, isExpense: Boolean, nameNote: String? = null) {
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            try {
                val maxOrder = repository.getMaxDisplayOrder(currentUserId, isExpense) ?: -1
                val newDisplayOrder = maxOrder + 1

                val category = CategoryEntity(
                    userId = currentUserId,
                    nameVi = name,
                    nameEn = name,
                    iconName = iconName,
                    isExpense = isExpense,
                    isActive = true,
                    nameNote = nameNote,
                    displayOrder = newDisplayOrder
                )
                repository.insertCategory(category)
                loadCategories() // Reload to get correct IDs
            } catch (e: Exception) {
                _error.value = "Không thể thêm danh mục: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(category)

                // Reorder remaining categories
                val remainingCategories = if (category.isExpense) {
                    repository.getExpenseCategories(currentUserId)
                } else {
                    repository.getIncomeCategories(currentUserId)
                }

                if (remainingCategories.isNotEmpty()) {
                    val updatedCategories = remainingCategories.mapIndexed { index, cat ->
                        cat.copy(displayOrder = index)
                    }
                    repository.updateCategories(updatedCategories)
                }

                loadCategories()
            } catch (e: Exception) {
                _error.value = "Không thể xóa danh mục: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun reorderCategories(fromIndex: Int, toIndex: Int, isExpense: Boolean) {
        if (fromIndex == toIndex) return

        viewModelScope.launch {
            try {
                val currentList = if (isExpense) {
                    _expenseCategories.value.toMutableList()
                } else {
                    _incomeCategories.value.toMutableList()
                }

                if (fromIndex !in currentList.indices || toIndex !in currentList.indices) {
                    _error.value = "Vị trí không hợp lệ"
                    return@launch
                }

                val item = currentList.removeAt(fromIndex)
                currentList.add(toIndex, item)

                val updatedCategories = currentList.mapIndexed { index, category ->
                    category.copy(displayOrder = index)
                }

                if (isExpense) {
                    _expenseCategories.value = updatedCategories
                } else {
                    _incomeCategories.value = updatedCategories
                }

                repository.updateCategories(updatedCategories)

            } catch (e: Exception) {
                _error.value = "Không thể sắp xếp lại: ${e.message}"
                e.printStackTrace()
                loadCategories()
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    suspend fun getActiveCategories(isExpense: Boolean): List<CategoryEntity> {
        return if (currentUserId.isEmpty()) {
            emptyList()
        } else {
            repository.getActiveCategories(currentUserId, isExpense)
        }
    }
}