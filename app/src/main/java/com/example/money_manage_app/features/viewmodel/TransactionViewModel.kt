package com.example.money_manage_app.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money_manage_app.data.local.entity.TransactionEntity
import com.example.money_manage_app.data.local.entity.TransactionWithCategory
import com.example.money_manage_app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import java.util.Calendar

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionWithCategory>>(emptyList())
    val transactions: StateFlow<List<TransactionWithCategory>> = _transactions.asStateFlow()

    private val _selectedTransaction = MutableStateFlow<TransactionWithCategory?>(null)
    val selectedTransaction: StateFlow<TransactionWithCategory?> = _selectedTransaction

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentUserId: String = ""
    private var collectJob: Job? = null

    // ✅ Set userId và bắt đầu observe transactions
    fun setUserId(userId: String) {
        if (currentUserId != userId && userId.isNotEmpty()) {
            currentUserId = userId
            observeTransactions()
        }
    }

    // ✅ Observe transactions từ database (tự động update khi có thay đổi)
    private fun observeTransactions() {
        // Hủy job cũ nếu có
        collectJob?.cancel()

        collectJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllTransactions(currentUserId).collect { list ->
                    _transactions.value = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Không thể tải giao dịch: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // ✅ Load transaction chi tiết
    fun loadTransactionById(id: Int) {
        viewModelScope.launch {
            try {
                _selectedTransaction.value = repository.getTransactionById(id)
            } catch (e: Exception) {
                _error.value = "Không thể tải chi tiết: ${e.message}"
            }
        }
    }

    // ✅ Thêm transaction
    suspend fun addTransaction(
        categoryId: Int,
        amount: Double,
        note: String,
        date: Long,
        isIncome: Boolean
    ): Boolean {
        android.util.Log.d("TransactionViewModel", "addTransaction called")
        android.util.Log.d("TransactionViewModel", "currentUserId: $currentUserId")

        if (currentUserId.isEmpty()) {
            android.util.Log.e("TransactionViewModel", "UserId is empty!")
            return false
        }

        return try {
            val transaction = TransactionEntity(
                userId = currentUserId,
                categoryId = categoryId,
                amount = amount,
                note = note,
                date = date,
                isIncome = isIncome
            )
            android.util.Log.d("TransactionViewModel", "Inserting transaction: $transaction")
            val rowId = repository.insertTransaction(transaction)
            android.util.Log.d("TransactionViewModel", "Insert result rowId: $rowId")
            // ✅ Room sẽ tự động trigger Flow update
            true
        } catch (e: Exception) {
            android.util.Log.e("TransactionViewModel", "Error inserting transaction", e)
            _error.value = "Không thể thêm giao dịch: ${e.message}"
            false
        }
    }

    // ✅ Cập nhật transaction
    suspend fun updateTransaction(
        id: Int,
        categoryId: Int,
        amount: Double,
        note: String,
        date: Long,
        isIncome: Boolean
    ): Boolean {
        if (currentUserId.isEmpty()) return false

        return try {
            val transaction = TransactionEntity(
                id = id,
                userId = currentUserId,
                categoryId = categoryId,
                amount = amount,
                note = note,
                date = date,
                isIncome = isIncome
            )
            repository.updateTransaction(transaction)
            // ✅ Reload chi tiết nếu đang xem
            if (_selectedTransaction.value?.transaction?.id == id) {
                loadTransactionById(id)
            }
            true
        } catch (e: Exception) {
            _error.value = "Không thể cập nhật: ${e.message}"
            false
        }
    }

    // ✅ Xóa transaction
    suspend fun deleteTransaction(transaction: TransactionEntity): Boolean {
        return try {
            repository.deleteTransaction(transaction)
            // ✅ Flow sẽ tự động cập nhật danh sách
            true
        } catch (e: Exception) {
            _error.value = "Không thể xóa: ${e.message}"
            false
        }
    }

    // ✅ Tính tổng thu/chi theo khoảng thời gian
    suspend fun getTotalIncome(startDate: Long, endDate: Long): Double {
        return if (currentUserId.isNotEmpty()) {
            repository.getTotalIncome(currentUserId, startDate, endDate)
        } else 0.0
    }

    suspend fun getTotalExpense(startDate: Long, endDate: Long): Double {
        return if (currentUserId.isNotEmpty()) {
            repository.getTotalExpense(currentUserId, startDate, endDate)
        } else 0.0
    }

    // ✅ Helper function: Get start/end of day
    fun getStartOfDay(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getEndOfDay(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    fun clearError() {
        _error.value = null
    }

    // ✅ Cleanup khi ViewModel bị destroy
    override fun onCleared() {
        super.onCleared()
        collectJob?.cancel()
    }
}