package com.jalSanchay.tracker.data.db

import androidx.room.*
import com.jalSanchay.tracker.data.model.RainfallEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface RainfallEntryDao {
    @Insert
    suspend fun insertEntry(entry: RainfallEntry): Long

    @Update
    suspend fun updateEntry(entry: RainfallEntry)

    @Delete
    suspend fun deleteEntry(entry: RainfallEntry)

    @Query("SELECT * FROM rainfall_entries WHERE userId = :userId ORDER BY date DESC")
    fun getAllEntries(userId: Int): Flow<List<RainfallEntry>>

    @Query("SELECT * FROM rainfall_entries WHERE userId = :userId ORDER BY date DESC LIMIT 3")
    fun getRecentEntries(userId: Int): Flow<List<RainfallEntry>>

    @Query("SELECT * FROM rainfall_entries WHERE userId = :userId AND date BETWEEN :from AND :to ORDER BY date DESC")
    fun getEntriesByDateRange(userId: Int, from: String, to: String): Flow<List<RainfallEntry>>

    @Query("SELECT SUM(litersHarvested) FROM rainfall_entries WHERE userId = :userId")
    fun getTotalHarvest(userId: Int): Flow<Float?>

    @Query("SELECT SUM(rainfallMm) FROM rainfall_entries WHERE userId = :userId")
    fun getTotalRainfall(userId: Int): Flow<Float?>

    @Query("SELECT SUM(litersHarvested) FROM rainfall_entries WHERE userId = :userId AND date LIKE :monthPrefix || '%'")
    fun getMonthlyHarvest(userId: Int, monthPrefix: String): Flow<Float?>

    @Query("DELETE FROM rainfall_entries WHERE userId = :userId")
    suspend fun deleteAllEntriesForUser(userId: Int)

    @Query("SELECT COUNT(*) FROM rainfall_entries WHERE userId = :userId")
    fun getEntryCount(userId: Int): Flow<Int>
}
