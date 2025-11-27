package com.example.money_manage_app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.money_manage_app.data.local.database.dao.UserDao
import com.example.money_manage_app.data.local.database.dao.CategoryDao
import com.example.money_manage_app.data.local.entity.User
import com.example.money_manage_app.data.local.entity.CategoryEntity

@Database(entities = [User::class, CategoryEntity::class], version = 3) // tăng version
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
}
