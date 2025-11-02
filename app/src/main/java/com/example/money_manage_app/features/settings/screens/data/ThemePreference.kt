package com.example.money_manage_app.features.settings.screens.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 🔧 Khai báo DataStore toàn cục cho Theme & Language
val Context.themeDataStore by preferencesDataStore("theme_prefs")

class ThemePreference(private val context: Context) {

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    // 🌙 Lưu trạng thái Dark Mode
    val isDarkMode: Flow<Boolean> = context.themeDataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    // 🌐 Lưu ngôn ngữ hiện tại
    val currentLanguage: Flow<String> = context.themeDataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "Tiếng Việt"
    }

    // 🔧 Hàm set Dark Mode
    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    // 🌍 Hàm set Ngôn ngữ
    suspend fun setLanguage(lang: String) {
        context.themeDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = lang
        }
    }
}
