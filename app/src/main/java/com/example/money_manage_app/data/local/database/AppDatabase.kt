package com.example.money_manage_app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.money_manage_app.data.local.database.dao.UserDao
import com.example.money_manage_app.data.local.entity.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
