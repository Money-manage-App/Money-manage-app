package com.example.money_manage_app.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

class CurrencyPreference(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val CURRENCY_KEY = stringPreferencesKey("currency_setting")
    }

    val currentCurrency: Flow<String> = dataStore.data.map {
        it[CURRENCY_KEY] ?: "VND"
    }

    suspend fun setCurrency(symbol: String) {
        dataStore.edit { it[CURRENCY_KEY] = symbol }
    }
}
