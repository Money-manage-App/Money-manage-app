package com.example.money_manage_app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.money_manage_app.data.local.database.dao.CategoryDao
import com.example.money_manage_app.data.local.database.dao.UserDao
import com.example.money_manage_app.data.local.database.dao.TransactionDao
import com.example.money_manage_app.data.local.entity.CategoryEntity
import com.example.money_manage_app.data.local.entity.User
import com.example.money_manage_app.data.local.entity.TransactionEntity

@Database(
    entities = [
        User::class,
        CategoryEntity::class,
        TransactionEntity::class
    ],
    version = 2, // ✅ Tăng version vì thêm bảng mới
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao // ✅ Thêm DAO mới

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
                    .fallbackToDestructiveMigration() // ✅ Tự động migrate (xóa data cũ)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}