package com.example.money_manage_app.data.repository

import android.util.Log
import com.example.money_manage_app.data.local.database.dao.CategoryDao
import com.example.money_manage_app.data.local.entity.CategoryEntity

class CategoryRepository(private val dao: CategoryDao) {

    // ✅ Fixed: Ensures proper cleanup and reload
    suspend fun fixCategoriesWithZeroId(userId: String) {
        try {
            val allCategories = dao.getExpenseCategories(userId) + dao.getIncomeCategories(userId)

            // Check if any category has ID = 0
            val hasZeroIds = allCategories.any { it.id == 0 }

            if (hasZeroIds) {
                Log.w("CategoryRepo", "❌ Found categories with ID = 0, fixing...")

                // Delete ALL old categories
                allCategories.forEach { dao.deleteCategory(it) }

                // Create new categories - Room will auto-generate IDs starting from 1
                val newCategories = listOf(
                    // EXPENSES
                    CategoryEntity(nameVi = "Ăn uống", nameEn = "Food", iconName = "Restaurant", isExpense = true, userId = userId, displayOrder = 0),
                    CategoryEntity(nameVi = "Giải trí", nameEn = "Entertainment", iconName = "Movie", isExpense = true, userId = userId, displayOrder = 1),
                    CategoryEntity(nameVi = "Mua sắm", nameEn = "Shopping", iconName = "ShoppingCart", isExpense = true, userId = userId, displayOrder = 2),
                    CategoryEntity(nameVi = "Di chuyển", nameEn = "Transport", iconName = "DirectionsCar", isExpense = true, userId = userId, displayOrder = 3),
                    CategoryEntity(nameVi = "Y tế", nameEn = "Health", iconName = "LocalGasStation", isExpense = true, userId = userId, displayOrder = 4),
                    CategoryEntity(nameVi = "Học tập", nameEn = "Education", iconName = "Build", isExpense = true, userId = userId, displayOrder = 5),
                    CategoryEntity(nameVi = "Nhà cửa", nameEn = "Housing", iconName = "Home", isExpense = true, userId = userId, displayOrder = 6),
                    CategoryEntity(nameVi = "Thú cưng", nameEn = "Pets", iconName = "Pets", isExpense = true, userId = userId, displayOrder = 7),
                    CategoryEntity(nameVi = "Khác", nameEn = "Other", iconName = "MoreHoriz", isExpense = true, userId = userId, displayOrder = 8),

                    // INCOME
                    CategoryEntity(nameVi = "Lương", nameEn = "Salary", iconName = "AttachMoney", isExpense = false, userId = userId, displayOrder = 0),
                    CategoryEntity(nameVi = "Thưởng", nameEn = "Bonus", iconName = "Star", isExpense = false, userId = userId, displayOrder = 1),
                    CategoryEntity(nameVi = "Đầu tư", nameEn = "Investment", iconName = "TrendingUp", isExpense = false, userId = userId, displayOrder = 2),
                    CategoryEntity(nameVi = "Khác", nameEn = "Other", iconName = "Money", isExpense = false, userId = userId, displayOrder = 3)
                )

                // Insert new categories with auto-generated IDs
                dao.insertAll(newCategories)

                Log.d("CategoryRepo", "✅ Successfully recreated categories!")

                // Verify the fix
                val verifyList = dao.getExpenseCategories(userId) + dao.getIncomeCategories(userId)
                verifyList.forEach {
                    Log.d("CategoryRepo", "  Category: ${it.nameVi}, ID: ${it.id}")
                }
            } else {
                Log.d("CategoryRepo", "✅ Categories OK, no fix needed")
            }
        } catch (e: Exception) {
            Log.e("CategoryRepo", "❌ Error fixing categories", e)
            throw e // Re-throw to let ViewModel handle it
        }
    }

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