package com.example.money_manage_app.data.local.database.dao

import androidx.room.*
import com.example.money_manage_app.data.local.entity.TransactionEntity
import com.example.money_manage_app.data.local.entity.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): TransactionEntity?

    // ✅ Queries with Category information
    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionWithCategoryById(id: Int): TransactionWithCategory?

    @Transaction
    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY date DESC, created_at DESC")
    fun getAllTransactionsWithCategory(userId: String): Flow<List<TransactionWithCategory>>

    @Transaction
    @Query("""
        SELECT * FROM transactions 
        WHERE user_id = :userId 
        AND date >= :startDate 
        AND date < :endDate 
        ORDER BY date DESC, created_at DESC
    """)
    fun getTransactionsByDateWithCategory(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionWithCategory>>

    @Transaction
    @Query("""
        SELECT * FROM transactions 
        WHERE user_id = :userId 
        AND category_id = :categoryId 
        ORDER BY date DESC
    """)
    fun getTransactionsByCategoryWithDetails(
        userId: String,
        categoryId: Int
    ): Flow<List<TransactionWithCategory>>

    @Query("""
        SELECT * FROM transactions 
        WHERE user_id = :userId 
        AND is_income = :isIncome 
        ORDER BY date DESC
    """)
    fun getTransactionsByType(
        userId: String,
        isIncome: Boolean
    ): Flow<List<TransactionEntity>>

    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE user_id = :userId 
        AND is_income = :isIncome 
        AND date >= :startDate 
        AND date <= :endDate
    """)
    suspend fun getTotalAmount(
        userId: String,
        isIncome: Boolean,
        startDate: Long,
        endDate: Long
    ): Double?

    @Query("DELETE FROM transactions WHERE user_id = :userId")
    suspend fun deleteAllTransactions(userId: String)

    @Query("SELECT COUNT(*) FROM transactions WHERE user_id = :userId")
    suspend fun getTransactionCount(userId: String): Int
}