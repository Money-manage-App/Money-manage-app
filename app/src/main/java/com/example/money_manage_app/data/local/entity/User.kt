package com.example.money_manage_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val userId: String, // "guest" hoặc Google UID
    val name: String,
    val email: String?,
    val phone: String?,
    val gender: String?,
    val photo: String?,
    val isGuest: Boolean,
    val createdAt: Long
)
