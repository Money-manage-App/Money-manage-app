package com.example.money_manage_app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name_vi")
    val nameVi: String,

    @ColumnInfo(name = "name_en")
    val nameEn: String,

    @ColumnInfo(name = "icon_name")
    val iconName: String,

    @ColumnInfo(name = "is_expense")
    val isExpense: Boolean,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "name_note")
    val nameNote: String? = null,

    @ColumnInfo(name = "display_order")
    val displayOrder: Int = 0  // Thêm field này
)