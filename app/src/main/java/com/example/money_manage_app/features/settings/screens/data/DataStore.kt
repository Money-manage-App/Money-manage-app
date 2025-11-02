package com.example.money_manage_app.features.settings.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// ⚙️ DataStore dùng chung cho toàn app (theme, language, font size,...)
val Context.appDataStore by preferencesDataStore(name = "app_settings")
