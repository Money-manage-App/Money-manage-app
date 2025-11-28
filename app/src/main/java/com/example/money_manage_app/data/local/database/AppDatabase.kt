package com.example.money_manage_app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.money_manage_app.data.local.database.dao.CategoryDao
import com.example.money_manage_app.data.local.database.dao.TransactionDao
import com.example.money_manage_app.data.local.database.dao.UserDao
import com.example.money_manage_app.data.local.entity.CategoryEntity
import com.example.money_manage_app.data.local.entity.TransactionEntity
import com.example.money_manage_app.data.local.entity.User

@Database(
    entities = [
        User::class,
        CategoryEntity::class,
        TransactionEntity::class
    ],
    version = 3,  // ✅ TĂNG VERSION từ 2 lên 3
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "money_manage_database"
                )
                    .fallbackToDestructiveMigration()  // ✅ Tạo lại DB với schema mới
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}