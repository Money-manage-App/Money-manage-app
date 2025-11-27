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

    // ✅ Lưu userId hiện tại
    private var currentUserId: String = ""

    // ✅ Set userId và load categories
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
                    // ✅ Kiểm tra user này đã có categories chưa
                    val totalCount = repository.countCategories(currentUserId)

                    if (totalCount == 0) {
                        // ✅ User mới → Tạo danh mục mẫu
                        val defaultCategories = DefaultCategories.createDefaultCategoriesForUser(currentUserId)
                        repository.insertAll(defaultCategories)

                        _expenseCategories.value = defaultCategories.filter { it.isExpense }
                        _incomeCategories.value = defaultCategories.filter { !it.isExpense }
                    } else {
                        // ✅ User cũ → Load categories đã lưu
                        _expenseCategories.value = repository.getExpenseCategories(currentUserId)
                        _incomeCategories.value = repository.getIncomeCategories(currentUserId)
                    }
                } catch (e: Exception) {
                    _error.value = "Không thể tải danh mục: ${e.message}"
                    e.printStackTrace()
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
                loadCategories()
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

                // ✅ Reorder các categories còn lại
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

    // ✅ THÊM: Method để get active categories cho AddTransaction screen
    suspend fun getActiveCategories(isExpense: Boolean): List<CategoryEntity> {
        return if (currentUserId.isEmpty()) {
            emptyList()
        } else {
            repository.getActiveCategories(currentUserId, isExpense)
        }
    }
}