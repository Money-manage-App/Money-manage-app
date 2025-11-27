package com.example.money_manage_app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.example.money_manage_app.data.local.entity.CategoryEntity

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category WHERE user_id = :userId AND is_expense = 1 ORDER BY display_order ASC")
    suspend fun getExpenseCategories(userId: String): List<CategoryEntity>

    @Query("SELECT * FROM category WHERE user_id = :userId AND is_expense = 0 ORDER BY display_order ASC")
    suspend fun getIncomeCategories(userId: String): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM category WHERE user_id = :userId AND is_active = 1 AND is_expense = :isExpense ORDER BY display_order ASC")
    suspend fun getActiveCategories(userId: String, isExpense: Boolean): List<CategoryEntity>

    @Query("SELECT COUNT(*) FROM category WHERE user_id = :userId")
    suspend fun countCategories(userId: String): Int

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategories(categories: List<CategoryEntity>)

    @Query("SELECT MAX(display_order) FROM category WHERE user_id = :userId AND is_expense = :isExpense")
    suspend fun getMaxDisplayOrder(userId: String, isExpense: Boolean): Int?

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

}