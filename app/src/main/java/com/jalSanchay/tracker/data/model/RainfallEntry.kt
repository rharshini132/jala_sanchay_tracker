package com.jalSanchay.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rainfall_entries")
data class RainfallEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val date: String,
    val rainfallMm: Float,
    val litersHarvested: Float,
    val tankLevelAfterLiters: Float,
    val source: String,
    val notes: String = "",
    val roofAreaUsed: Float,
    val runoffUsed: Float,
    val createdAt: Long = System.currentTimeMillis()
)
