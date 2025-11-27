package com.example.money_manage_app.features.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money_manage_app.data.local.entity.User
import com.example.money_manage_app.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    // ✅ Context sẽ được truyền qua các method thay vì constructor

    // ✅ THÊM: Theo dõi userId hiện tại
    private val _currentUserId = MutableStateFlow<String>("guest")
    val currentUserId: StateFlow<String> = _currentUserId

    // ✅ THÊM: Theo dõi user hiện tại
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // ✅ Load userId từ SharedPreferences
    fun loadCurrentUser(context: Context) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = prefs.getString("current_user_id", "guest") ?: "guest"
            _currentUserId.value = userId

            if (userId != "guest") {
                _currentUser.value = repository.getUserOnce(userId)
            } else {
                _currentUser.value = null
            }
        }
    }

    // ✅ THÊM: Đăng nhập (lưu userId)
    fun login(context: Context, userId: String) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("current_user_id", userId).apply()
            _currentUserId.value = userId
            _currentUser.value = repository.getUserOnce(userId)
        }
    }

    // ✅ THÊM: Đăng xuất
    fun logout(context: Context) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("current_user_id", "guest").apply()
            _currentUserId.value = "guest"
            _currentUser.value = null
        }
    }

    fun getUser(userId: String): Flow<User?> =
        repository.getUser(userId)

    fun saveUser(user: User) {
        viewModelScope.launch {
            repository.saveUser(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun deleteUser(id: String) {
        viewModelScope.launch {
            repository.deleteUser(id)
        }
    }

    suspend fun getUserOnce(userId: String): User? {
        return repository.getUserOnce(userId)
    }
}