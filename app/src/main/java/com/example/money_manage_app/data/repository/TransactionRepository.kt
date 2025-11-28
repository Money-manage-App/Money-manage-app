package com.example.money_manage_app.data.repository

import com.example.money_manage_app.data.local.database.dao.TransactionDao
import com.example.money_manage_app.data.local.entity.TransactionEntity
import com.example.money_manage_app.data.local.entity.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {

    fun getAllTransactions(userId: String): Flow<List<TransactionWithCategory>> {
        return dao.getAllTransactionsWithCategory(userId)
    }

    fun getTransactionsByDate(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionWithCategory>> {
        return dao.getTransactionsByDateWithCategory(userId, startDate, endDate)
    }

    suspend fun getTransactionById(id: Int): TransactionWithCategory? {
        return dao.getTransactionWithCategoryById(id)
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return dao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        dao.updateTransaction(transaction.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }

    fun getTransactionsByCategory(
        userId: String,
        categoryId: Int
    ): Flow<List<TransactionWithCategory>> {
        return dao.getTransactionsByCategoryWithDetails(userId, categoryId)
    }

    fun getTransactionsByType(
        userId: String,
        isIncome: Boolean
    ): Flow<List<TransactionEntity>> {
        return dao.getTransactionsByType(userId, isIncome)
    }

    suspend fun getTotalIncome(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Double {
        return dao.getTotalAmount(userId, true, startDate, endDate) ?: 0.0
    }

    suspend fun getTotalExpense(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Double {
        return dao.getTotalAmount(userId, false, startDate, endDate) ?: 0.0
    }

    suspend fun deleteAllTransactions(userId: String) {
        dao.deleteAllTransactions(userId)
    }

    suspend fun getTransactionCount(userId: String): Int {
        return dao.getTransactionCount(userId)
    }
}