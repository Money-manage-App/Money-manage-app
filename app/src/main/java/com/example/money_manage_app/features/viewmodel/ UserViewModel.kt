package com.example.money_manage_app.features.viewmodel

import android.content.Context
import android.util.Log
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

    private val _currentUserId = MutableStateFlow<String>("guest")
    val currentUserId: StateFlow<String> = _currentUserId

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // ✅ Load userId từ SharedPreferences VÀ đảm bảo guest user tồn tại
    fun loadCurrentUser(context: Context) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "=== Loading Current User ===")

                // ✅ CRITICAL: Tạo guest user trước nếu chưa có
                repository.ensureGuestUserExists()

                val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val userId = prefs.getString("current_user_id", "guest") ?: "guest"

                Log.d("UserViewModel", "Loaded userId from prefs: '$userId'")
                _currentUserId.value = userId

                if (userId != "guest") {
                    val user = repository.getUserOnce(userId)
                    _currentUser.value = user
                    Log.d("UserViewModel", "Loaded user: ${user?.name}")
                } else {
                    // Load guest user info
                    val guestUser = repository.getUserOnce("guest")
                    _currentUser.value = guestUser
                    Log.d("UserViewModel", "Loaded guest user: ${guestUser?.userId}")
                }

                Log.d("UserViewModel", "✅ Current userId set to: '${_currentUserId.value}'")
            } catch (e: Exception) {
                Log.e("UserViewModel", "❌ Error loading user", e)
            }
        }
    }

    // ✅ Đăng nhập (lưu userId)
    fun login(context: Context, userId: String) {
        viewModelScope.launch {
            try {
                val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("current_user_id", userId).apply()
                _currentUserId.value = userId
                _currentUser.value = repository.getUserOnce(userId)
                Log.d("UserViewModel", "✅ Logged in as: $userId")
            } catch (e: Exception) {
                Log.e("UserViewModel", "❌ Error during login", e)
            }
        }
    }

    // ✅ Đăng xuất
    fun logout(context: Context) {
        viewModelScope.launch {
            try {
                // Ensure guest user exists before logout
                repository.ensureGuestUserExists()

                val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("current_user_id", "guest").apply()
                _currentUserId.value = "guest"
                _currentUser.value = repository.getUserOnce("guest")
                Log.d("UserViewModel", "✅ Logged out, switched to guest")
            } catch (e: Exception) {
                Log.e("UserViewModel", "❌ Error during logout", e)
            }
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