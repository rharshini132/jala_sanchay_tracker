package com.jalSanchay.tracker.data.db

import androidx.room.*
import com.jalSanchay.tracker.data.model.TankSetup

@Dao
interface TankSetupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(setup: TankSetup)

    @Query("SELECT * FROM tank_setup WHERE userId = :userId LIMIT 1")
    suspend fun getSetupByUser(userId: Int): TankSetup?

    @Query("UPDATE tank_setup SET currentWaterLevelLiters = :level, updatedAt = :time WHERE userId = :userId")
    suspend fun updateWaterLevel(userId: Int, level: Float, time: Long)

    @Query("DELETE FROM tank_setup WHERE userId = :userId")
    suspend fun deleteByUser(userId: Int)
}
