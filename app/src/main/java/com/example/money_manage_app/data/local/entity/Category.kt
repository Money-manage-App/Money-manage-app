package com.example.money_manage_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import androidx.room.Ignore
import androidx.compose.ui.graphics.vector.ImageVector

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name_vi")
    val nameVi: String,

    @ColumnInfo(name = "name_en")
    val nameEn: String,

    @ColumnInfo(name = "icon_name")
    val iconName: String, // Lưu tên icon để mapping khi load

    @ColumnInfo(name = "is_expense")
    val isExpense: Boolean = true,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
