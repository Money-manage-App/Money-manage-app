package com.example.money_manage_app.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class CategoryData(
    val iconName: String,
    val name: String
)

class CategoryPreference(private val context: Context) {

    private val KEY = stringPreferencesKey("categories")

    private val jsonFormatter = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    // Danh mục mặc định
    private val defaultExpenseCategories = listOf(
        CategoryData("ShoppingCart", "Mua sắm"),
        CategoryData("SportsEsports", "Giải trí"),
        CategoryData("Checkroom", "Quần áo"),
        CategoryData("Pets", "Thú cưng"),
        CategoryData("Restaurant", "Đồ ăn"),
        CategoryData("SportsSoccer", "Thể thao"),
        CategoryData("Favorite", "Sức khỏe"),
        CategoryData("Build", "Sửa chữa"),
        CategoryData("CardGiftcard", "Biếu tặng")
    )

    private val defaultIncomeCategories = listOf(
        CategoryData("Money", "Lương"),
        CategoryData("Savings", "Khoản đầu tư"),
        CategoryData("Schedule", "Bán thời gian"),
        CategoryData("MoreHoriz", "Khác")
    )

    // Flow trả về Map<String, List<CategoryData>>
    val categoriesFlow: Flow<Map<String, List<CategoryData>>> = context.appDataStore.data.map { preferences ->
        val json = preferences[KEY] ?: ""
        if (json.isEmpty()) {
            mapOf(
                "expense" to defaultExpenseCategories,
                "income" to defaultIncomeCategories
            )
        } else {
            try {
                Json { ignoreUnknownKeys = true }
                    .decodeFromString<Map<String, List<CategoryData>>>(json)
            } catch (e: Exception) {
                mapOf(
                    "expense" to defaultExpenseCategories,
                    "income" to defaultIncomeCategories
                )
            }
        }
    }

    // Hàm lưu Map<String, List<CategoryData>> vào DataStore
    suspend fun save(data: Map<String, List<CategoryData>>) {
        context.appDataStore.edit { preferences ->
            preferences[KEY] = jsonFormatter.encodeToString(data)
        }
    }

    // Hàm reset về mặc định
    suspend fun resetToDefault() {
        save(
            mapOf(
                "expense" to defaultExpenseCategories,
                "income" to defaultIncomeCategories
            )
        )
    }

    // Hàm thêm mới danh mục
    suspend fun saveNewCategory(type: String, category: CategoryData) {
        val current = categoriesFlow.first()
        val updated = current.toMutableMap()
        val list = updated[type]?.toMutableList() ?: mutableListOf()
        list.add(category)
        updated[type] = list
        save(updated)
    }
}
