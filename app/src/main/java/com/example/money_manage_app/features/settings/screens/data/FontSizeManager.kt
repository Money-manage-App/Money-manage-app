package com.example.money_manage_app.features.settings.data

import android.content.Context
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FontSizeManager(private val context: Context) {

    companion object {
        private val FONT_SIZE_KEY = floatPreferencesKey("font_size")
    }

    val fontSizeFlow: Flow<Float> = context.appDataStore.data.map { prefs ->
        prefs[FONT_SIZE_KEY] ?: 1f
    }

    suspend fun setFontSize(scale: Float) {
        context.appDataStore.edit { prefs ->
            prefs[FONT_SIZE_KEY] = scale
        }
    }
}
