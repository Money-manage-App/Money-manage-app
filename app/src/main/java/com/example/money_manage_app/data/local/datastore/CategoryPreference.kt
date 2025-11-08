package com.example.money_manage_app.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CategoryPreference(private val context: Context) {
    private val KEY = stringPreferencesKey("categories")

    val categoriesFlow = context.appDataStore.data.map {
        val json = it[KEY] ?: "[]"
        Json.decodeFromString<List<String>>(json)
    }

    suspend fun save(list: List<String>) {
        context.appDataStore.edit {
            it[KEY] = Json.encodeToString(list)
        }
    }
}
