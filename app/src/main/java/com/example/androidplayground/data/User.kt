package com.example.androidplayground.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val name: String,
    val photoUrl: String? = null,
    val isLoggedIn: Boolean = true,
    val lastLoginTime: Long = System.currentTimeMillis()
) 