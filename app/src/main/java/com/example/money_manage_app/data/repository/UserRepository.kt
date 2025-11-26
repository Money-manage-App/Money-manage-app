package com.example.money_manage_app.data.repository

import com.example.money_manage_app.data.local.database.dao.UserDao
import com.example.money_manage_app.data.local.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UserRepository(private val userDao: UserDao) {

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
        return userDao.getUser(userId).firstOrNull() // Flow -> lấy 1 giá trị đầu tiên
    }

}
