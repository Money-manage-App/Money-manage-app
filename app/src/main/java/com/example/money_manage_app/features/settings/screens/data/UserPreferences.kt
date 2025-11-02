package com.example.money_manage_app.features.settings.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val NAME = stringPreferencesKey("name")
        val EMAIL = stringPreferencesKey("email")
        val PHONE = stringPreferencesKey("phone")
    }

    val userInfo: Flow<Map<String, String>> = context.dataStore.data.map {
        mapOf(
            "name" to (it[NAME] ?: ""),
            "email" to (it[EMAIL] ?: ""),
            "phone" to (it[PHONE] ?: "")
        )
    }

    suspend fun saveUserInfo(name: String, email: String, phone: String) {
        context.dataStore.edit {
            it[NAME] = name
            it[EMAIL] = email
            it[PHONE] = phone
        }
    }
}
