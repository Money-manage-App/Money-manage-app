package com.example.money_manage_app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.money_manage_app.data.local.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category WHERE is_expense = 1")
    suspend fun getExpenseCategories(): List<CategoryEntity>

    @Query("SELECT * FROM category WHERE is_expense = 0")
    suspend fun getIncomeCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM category WHERE is_active = 1 AND is_expense = :isExpense")
    suspend fun getActiveCategories(isExpense: Boolean): List<CategoryEntity>


}
