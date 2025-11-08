package com.example.money_manage_app.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// ⚙️ DataStore dùng chung cho toàn app (theme, language, font size,...)
val Context.appDataStore by preferencesDataStore(name = "app_settings")
