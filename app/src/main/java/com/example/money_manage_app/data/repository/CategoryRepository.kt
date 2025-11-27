package com.example.money_manage_app.data.repository

import com.example.money_manage_app.data.local.database.dao.CategoryDao
import com.example.money_manage_app.data.local.entity.CategoryEntity

class CategoryRepository(private val dao: CategoryDao) {

    suspend fun insertCategory(category: CategoryEntity) {
        dao.insertAll(listOf(category))
    }

    // ✅ Đã có ORDER BY trong DAO
    suspend fun getExpenseCategories() = dao.getExpenseCategories()

    suspend fun getIncomeCategories() = dao.getIncomeCategories()

    suspend fun getActiveCategories(isExpense: Boolean) = dao.getActiveCategories(isExpense)

    suspend fun insertAll(categories: List<CategoryEntity>) {
        dao.insertAll(categories)
    }

    suspend fun countCategories(): Int = dao.countCategories()

    suspend fun deleteCategory(category: CategoryEntity) = dao.deleteCategory(category)

    // ✅ SỬ DỤNG @Update từ DAO
    suspend fun updateCategory(category: CategoryEntity) {
        dao.updateCategory(category)
    }

    // ✅ SỬ DỤNG @Transaction @Update từ DAO
    suspend fun updateCategories(categories: List<CategoryEntity>) {
        dao.updateCategories(categories)
    }

    // ✅ THÊM method mới
    suspend fun getMaxDisplayOrder(isExpense: Boolean): Int? {
        return dao.getMaxDisplayOrder(isExpense)
    }

    suspend fun getCategoryById(id: Int): CategoryEntity? {
        return dao.getCategoryById(id)
    }
}