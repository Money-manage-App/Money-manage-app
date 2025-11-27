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
    // ✅ THÊM ORDER BY display_order để đảm bảo thứ tự đúng
    @Query("SELECT * FROM category WHERE is_expense = 1 ORDER BY display_order ASC")
    suspend fun getExpenseCategories(): List<CategoryEntity>

    @Query("SELECT * FROM category WHERE is_expense = 0 ORDER BY display_order ASC")
    suspend fun getIncomeCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM category WHERE is_active = 1 AND is_expense = :isExpense ORDER BY display_order ASC")
    suspend fun getActiveCategories(isExpense: Boolean): List<CategoryEntity>

    @Query("SELECT COUNT(*) FROM category")
    suspend fun countCategories(): Int

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    // ✅ THÊM @Update để rõ ràng hơn
    @Update
    suspend fun updateCategory(category: CategoryEntity)

    // ✅ THÊM @Transaction để đảm bảo atomic operation
    @Transaction
    @Update
    suspend fun updateCategories(categories: List<CategoryEntity>)

    // ✅ THÊM query để lấy max displayOrder (hữu ích khi thêm category mới)
    @Query("SELECT MAX(display_order) FROM category WHERE is_expense = :isExpense")
    suspend fun getMaxDisplayOrder(isExpense: Boolean): Int?

    // ✅ THÊM query để lấy category theo ID (useful for future features)
    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryEntity?
}