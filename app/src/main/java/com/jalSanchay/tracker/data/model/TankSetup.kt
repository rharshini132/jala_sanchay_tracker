package com.jalSanchay.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tank_setup")
data class TankSetup(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val roofAreaM2: Float,
    val roofMaterial: String,
    val runoffCoefficient: Float,
    val tankCapacityLiters: Float,
    val tankMaterial: String,
    val currentWaterLevelLiters: Float = 0f,
    val updatedAt: Long = System.currentTimeMillis()
)
