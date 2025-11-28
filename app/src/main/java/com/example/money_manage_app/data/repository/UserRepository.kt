package com.example.money_manage_app.data.repository

import android.util.Log
import com.example.money_manage_app.data.local.database.dao.UserDao
import com.example.money_manage_app.data.local.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UserRepository(private val userDao: UserDao) {

    // ✅ CRITICAL: Đảm bảo guest user tồn tại trong database
    suspend fun ensureGuestUserExists() {
        try {
            val guestUser = userDao.getUser("guest").firstOrNull()
            if (guestUser == null) {
                val newGuestUser = User(
                    userId = "guest",
                    name = "Guest User",
                    email = null,
                    phone = null,
                    gender = null,
                    photo = null,
                    isGuest = true,
                    createdAt = System.currentTimeMillis()
                )
                userDao.insertUser(newGuestUser)
                Log.d("UserRepository", "✅ Created guest user in database")
            } else {
                Log.d("UserRepository", "✅ Guest user already exists: ${guestUser.userId}")
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error ensuring guest user", e)
            throw e
        }
    }

    // Lấy user theo UID/email
    fun getUser(userId: String): Flow<User?> = userDao.getUser(userId)

    // Lấy user theo email
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    // Lấy hoặc tạo user nếu chưa có
    suspend fun getOrCreateUser(
        email: String,
        name: String,
        phone: String?,
        gender: Boolean?,
        photo: String?
    ): User {
        var user = userDao.getUserByEmail(email)
        if (user == null) {
            user = User(
                userId = email, // dùng email làm key nếu UID chưa có
                name = name,
                email = email,
                phone = phone,
                gender = gender,
                photo = photo,
                isGuest = false,
                createdAt = System.currentTimeMillis()
            )
            userDao.insertUser(user)
        }
        return user
    }

    suspend fun saveUser(user: User) = userDao.insertUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun deleteUser(userId: String) = userDao.deleteUser(userId)

    suspend fun getUserOnce(userId: String): User? {
        return userDao.getUser(userId).firstOrNull()
    }
}