package com.jalSanchay.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val city: String,
    val householdSize: String,
    val createdAt: Long = System.currentTimeMillis()
)
