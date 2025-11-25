package com.example.money_manage_app.data.repository

import com.example.money_manage_app.data.local.database.dao.UserDao
import com.example.money_manage_app.data.local.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    fun getUser(userId: String): Flow<User?> =
        userDao.getUser(userId)

    suspend fun saveUser(user: User) =
        userDao.insertUser(user)

    suspend fun updateUser(user: User) =
        userDao.updateUser(user)

    suspend fun deleteUser(userId: String) =
        userDao.deleteUser(userId)
}
