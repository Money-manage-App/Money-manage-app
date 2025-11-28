package com.example.money_manage_app

import android.app.Application
import androidx.room.Room
import com.example.money_manage_app.data.local.database.AppDatabase
import kotlin.jvm.java

class MyApp : Application() {

    companion object {
        lateinit var db: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        // Khởi tạo Room database
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "money_manage_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
