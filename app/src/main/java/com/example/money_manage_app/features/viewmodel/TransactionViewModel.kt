package com.example.money_manage_app.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money_manage_app.data.local.entity.TransactionEntity
import com.example.money_manage_app.data.local.entity.TransactionWithCategory
import com.example.money_manage_app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import java.util.Calendar

private const val TAG = "TransactionViewModel"

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionWithCategory>>(emptyList())
    val transactions: StateFlow<List<TransactionWithCategory>> = _transactions

    private val _selectedTransaction = MutableStateFlow<TransactionWithCategory?>(null)
    val selectedTransaction: StateFlow<TransactionWithCategory?> = _selectedTransaction

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // userId cho giao dịch
    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private var observeJob: Job? = null


    // ============================================================
    // 🔥 FIX 1: KHÔNG BAO GIỜ XÓA TRANSACTIONS KHI SET USER
    // 🔥 FIX 2: CHỈ START OBSERVE KHI USERID CÓ THẬT
    // ============================================================
    fun setUserId(userId: String) {
        if (userId.isBlank()) return
        if (_currentUserId.value == userId) return   // user giữ nguyên → không restart

        android.util.Log.d(TAG, "🔄 Switching user to: $userId")

        _currentUserId.value = userId

        // ❌ KHÔNG reset _transactions ở đây (không bao giờ làm!!)
        // ❌ KHÔNG clear data

        startObserving()
    }


    // ============================================================
    // 🔥 CHỈ OBSERVE TRANSACTION KHI USERID SẴN SÀNG
    // 🔥 KHÔNG BAO GIỜ RESET LIST KHI JOB CANCEL
    // ============================================================
    private fun startObserving() {
        observeJob?.cancel()

        val userId = _currentUserId.value
        android.util.Log.d(TAG, "📡 Observing transactions for user: $userId")

        observeJob = viewModelScope.launch {
            _isLoading.value = true

            // ✅ DEBUG: Check actual database count
            val dbCount = repository.getTransactionCount(userId)
            android.util.Log.d(TAG, "🔍 Database has $dbCount transactions for $userId")

            repository.getAllTransactions(userId)
                .catch { e ->
                    android.util.Log.e(TAG, "❌ Error observing transactions", e)
                    _error.value = "Không thể tải giao dịch"
                    _isLoading.value = false
                }
                .collect { list ->
                    android.util.Log.d(TAG, "📦 Received: ${list.size} transactions (DB reported: $dbCount)")
                    _transactions.value = list
                    _isLoading.value = false
                }
        }
    }


    // ============================================================
    // 🔥 THÊM TRANSACTION (KHÔNG BAO GIỜ MẤT)
    // ============================================================
    suspend fun addTransaction(
        categoryId: Int,
        amount: Double,
        note: String,
        date: Long,
        isIncome: Boolean
    ): Boolean {

        if (_currentUserId.value.isEmpty()) {
            android.util.Log.e(TAG, "❌ Cannot add transaction: userId empty")
            return false
        }

        val transaction = TransactionEntity(
            userId = _currentUserId.value,
            categoryId = categoryId,
            amount = amount,
            note = note,
            date = date,
            isIncome = isIncome
        )

        return try {
            android.util.Log.d(TAG, "📝 Inserting: $transaction")
            repository.insertTransaction(transaction)
            true
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Error inserting", e)
            _error.value = "Không thể thêm giao dịch"
            false
        }
    }


    // ============================================================
    // LOAD TRANSACTION DETAIL
    // ============================================================
    fun loadTransactionById(id: Int) {
        viewModelScope.launch {
            try {
                _selectedTransaction.value = repository.getTransactionById(id)
            } catch (e: Exception) {
                _error.value = "Không thể tải chi tiết"
            }
        }
    }


    // ============================================================
    // UPDATE
    // ============================================================
    suspend fun updateTransaction(
        id: Int,
        categoryId: Int,
        amount: Double,
        note: String,
        date: Long,
        isIncome: Boolean
    ): Boolean {

        return try {
            val transaction = TransactionEntity(
                id = id,
                userId = _currentUserId.value,
                categoryId = categoryId,
                amount = amount,
                note = note,
                date = date,
                isIncome = isIncome
            )

            repository.updateTransaction(transaction)

            if (_selectedTransaction.value?.transaction?.id == id)
                loadTransactionById(id)

            true
        } catch (e: Exception) {
            _error.value = "Không thể cập nhật"
            false
        }
    }


    // ============================================================
    // DELETE
    // ============================================================
    suspend fun deleteTransaction(transaction: TransactionEntity): Boolean {
        return try {
            repository.deleteTransaction(transaction)
            true
        } catch (e: Exception) {
            _error.value = "Không thể xóa"
            false
        }
    }


    // ============================================================
    // HELPER FUNCTIONS
    // ============================================================
    fun getStartOfDay(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getEndOfDay(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
    }

    suspend fun debugCheckTransactions(userId: String): Int {
        return try {
            val count = repository.getTransactionCount(userId)
            android.util.Log.d(TAG, "🔍 Database check: User $userId has $count transactions")
            count
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Error checking transaction count", e)
            0
        }
    }
}
