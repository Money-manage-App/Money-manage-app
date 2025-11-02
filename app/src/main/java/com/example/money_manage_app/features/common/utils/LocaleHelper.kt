package com.example.money_manage_app.features.common.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, language: String): Context {
        val locale = when (language) {
            "English" -> Locale("en")
            "Tiếng Việt" -> Locale("vi")
            else -> Locale.getDefault()
        }

        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    fun updateLocale(activity: Activity, language: String) {
        setLocale(activity, language)
        activity.recreate() // 🔁 restart lại activity
    }
}
