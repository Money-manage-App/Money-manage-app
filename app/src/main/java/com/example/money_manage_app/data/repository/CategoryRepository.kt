package com.example.money_manage_app.data.repository

import com.example.money_manage_app.data.local.database.dao.CategoryDao
import com.example.money_manage_app.data.local.entity.CategoryEntity

class CategoryRepository(private val dao: CategoryDao) {

    suspend fun insertCategory(category: CategoryEntity) {
        dao.insertAll(listOf(category))
    }

    suspend fun getExpenseCategories(userId: String) = dao.getExpenseCategories(userId)

    suspend fun getIncomeCategories(userId: String) = dao.getIncomeCategories(userId)

    suspend fun getActiveCategories(userId: String, isExpense: Boolean) =
        dao.getActiveCategories(userId, isExpense)

    suspend fun insertAll(categories: List<CategoryEntity>) {
        dao.insertAll(categories)
    }

    suspend fun countCategories(userId: String): Int = dao.countCategories(userId)

    suspend fun deleteCategory(category: CategoryEntity) = dao.deleteCategory(category)

    suspend fun updateCategory(category: CategoryEntity) {
        dao.updateCategory(category)
    }

    suspend fun updateCategories(categories: List<CategoryEntity>) {
        dao.updateCategories(categories)
    }

    suspend fun getMaxDisplayOrder(userId: String, isExpense: Boolean): Int? {
        return dao.getMaxDisplayOrder(userId, isExpense)
    }

    suspend fun getCategoryById(id: Int): CategoryEntity? {
        return dao.getCategoryById(id)
    }
}