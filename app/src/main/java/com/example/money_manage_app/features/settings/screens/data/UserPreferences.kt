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
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHONE = stringPreferencesKey("user_phone")

        val USER_GENDER = stringPreferencesKey("user_gender")
        val USER_BIRTHDAY = stringPreferencesKey("user_birthday")
    }

    val userInfo: Flow<Map<String, String>> = context.dataStore.data.map { prefs ->
        mapOf(
            "name" to (prefs[USER_NAME] ?: ""),
            "email" to (prefs[USER_EMAIL] ?: ""),
            "phone" to (prefs[USER_PHONE] ?: ""),
            "gender" to (prefs[USER_GENDER] ?: "Not set"),
            "birthday" to (prefs[USER_BIRTHDAY] ?: "Not set")
        )
    }

    suspend fun saveUserInfo(name: String, email: String, phone: String, gender: String, birthday: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_EMAIL] = email
            prefs[USER_PHONE] = phone
            prefs[USER_GENDER] = gender
            prefs[USER_BIRTHDAY] = birthday
        }
    }

    suspend fun clearUserInfo() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_NAME)
            prefs.remove(USER_EMAIL)
            prefs.remove(USER_PHONE)
            prefs.remove(USER_GENDER)
            prefs.remove(USER_BIRTHDAY)
        }
    }
}
