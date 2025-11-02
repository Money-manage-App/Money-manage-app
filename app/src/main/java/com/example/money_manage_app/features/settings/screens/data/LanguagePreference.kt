package com.example.money_manage_app.features.settings.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.languageDataStore by preferencesDataStore(name = "language_prefs")

class LanguagePreference(private val context: Context) {
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    val currentLanguage: Flow<String> = context.languageDataStore.data.map {
        it[LANGUAGE_KEY] ?: "Tiếng Việt" // Mặc định là Tiếng Việt
    }

    suspend fun setLanguage(language: String) {
        context.languageDataStore.edit {
            it[LANGUAGE_KEY] = language
        }
    }
}
