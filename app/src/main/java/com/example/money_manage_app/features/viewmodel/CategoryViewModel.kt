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

    // ✅ THÊM loading state để tránh multiple concurrent loads
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ✅ THÊM error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ✅ THÊM Mutex để đảm bảo thread-safe operations
    private val loadMutex = Mutex()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            loadMutex.withLock {
                // ✅ Tránh load nhiều lần cùng lúc
                if (_isLoading.value) return@withLock

                _isLoading.value = true
                _error.value = null

                try {
                    val totalCount = repository.countCategories()
                    if (totalCount == 0) {
                        val defaults = DefaultCategories.expenseCategories + DefaultCategories.incomeCategories
                        repository.insertAll(defaults)
                        _expenseCategories.value = DefaultCategories.expenseCategories
                        _incomeCategories.value = DefaultCategories.incomeCategories
                    } else {
                        // ✅ Database đã có ORDER BY display_order
                        _expenseCategories.value = repository.getExpenseCategories()
                        _incomeCategories.value = repository.getIncomeCategories()
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
        viewModelScope.launch {
            try {
                // ✅ SỬ DỤNG getMaxDisplayOrder thay vì load toàn bộ list
                val maxOrder = repository.getMaxDisplayOrder(isExpense) ?: -1
                val newDisplayOrder = maxOrder + 1

                val category = CategoryEntity(
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

                // ✅ Cập nhật lại displayOrder cho các categories còn lại
                val remainingCategories = if (category.isExpense) {
                    repository.getExpenseCategories()
                } else {
                    repository.getIncomeCategories()
                }

                // ✅ Chỉ update nếu cần thiết
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
        // ✅ Early return nếu không cần di chuyển
        if (fromIndex == toIndex) return

        viewModelScope.launch {
            try {
                val currentList = if (isExpense) {
                    _expenseCategories.value.toMutableList()
                } else {
                    _incomeCategories.value.toMutableList()
                }

                // ✅ Kiểm tra bounds
                if (fromIndex !in currentList.indices || toIndex !in currentList.indices) {
                    _error.value = "Vị trí không hợp lệ"
                    return@launch
                }

                // Di chuyển item
                val item = currentList.removeAt(fromIndex)
                currentList.add(toIndex, item)

                // ✅ Cập nhật displayOrder cho tất cả items
                val updatedCategories = currentList.mapIndexed { index, category ->
                    category.copy(displayOrder = index)
                }

                // ✅ Cập nhật UI ngay lập tức (optimistic update)
                if (isExpense) {
                    _expenseCategories.value = updatedCategories
                } else {
                    _incomeCategories.value = updatedCategories
                }

                // Lưu vào database
                repository.updateCategories(updatedCategories)

                // ✅ KHÔNG cần loadCategories() nữa vì đã update UI rồi
                // loadCategories() // Bỏ dòng này để tránh flicker

            } catch (e: Exception) {
                _error.value = "Không thể sắp xếp lại: ${e.message}"
                e.printStackTrace()
                // ✅ Nếu lỗi thì reload lại để đồng bộ
                loadCategories()
            }
        }
    }

    // ✅ THÊM method để clear error
    fun clearError() {
        _error.value = null
    }
}