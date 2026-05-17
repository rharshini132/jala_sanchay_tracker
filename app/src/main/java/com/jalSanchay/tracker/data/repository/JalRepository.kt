package com.jalSanchay.tracker.data.repository

import com.jalSanchay.tracker.data.db.RainfallEntryDao
import com.jalSanchay.tracker.data.db.TankSetupDao
import com.jalSanchay.tracker.data.db.UserDao
import com.jalSanchay.tracker.data.model.RainfallEntry
import com.jalSanchay.tracker.data.model.TankSetup
import com.jalSanchay.tracker.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JalRepository @Inject constructor(
    private val userDao: UserDao,
    private val tankSetupDao: TankSetupDao,
    private val rainfallEntryDao: RainfallEntryDao
) {
    // ==================== Harvest Formula ====================
    fun calculateHarvest(roofAreaM2: Float, rainfallMm: Float, runoffCoeff: Float): Float {
        return roofAreaM2 * rainfallMm * 0.0929f * runoffCoeff
    }

    // ==================== User Operations ====================
    suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    suspend fun getUserById(id: Int): User? = userDao.getUserById(id)

    // ==================== Tank Setup Operations ====================
    suspend fun insertOrUpdateSetup(setup: TankSetup) = tankSetupDao.insertOrUpdate(setup)
    suspend fun getSetupByUser(userId: Int): TankSetup? = tankSetupDao.getSetupByUser(userId)
    suspend fun updateWaterLevel(userId: Int, level: Float) =
        tankSetupDao.updateWaterLevel(userId, level, System.currentTimeMillis())
    suspend fun deleteSetupByUser(userId: Int) = tankSetupDao.deleteByUser(userId)

    // ==================== Rainfall Entry Operations ====================
    suspend fun insertEntry(entry: RainfallEntry): Long = rainfallEntryDao.insertEntry(entry)
    suspend fun updateEntry(entry: RainfallEntry) = rainfallEntryDao.updateEntry(entry)
    suspend fun deleteEntry(entry: RainfallEntry) = rainfallEntryDao.deleteEntry(entry)

    fun getAllEntries(userId: Int): Flow<List<RainfallEntry>> = rainfallEntryDao.getAllEntries(userId)
    fun getRecentEntries(userId: Int): Flow<List<RainfallEntry>> = rainfallEntryDao.getRecentEntries(userId)
    fun getEntriesByDateRange(userId: Int, from: String, to: String): Flow<List<RainfallEntry>> =
        rainfallEntryDao.getEntriesByDateRange(userId, from, to)

    fun getTotalHarvest(userId: Int): Flow<Float?> = rainfallEntryDao.getTotalHarvest(userId)
    fun getTotalRainfall(userId: Int): Flow<Float?> = rainfallEntryDao.getTotalRainfall(userId)
    fun getMonthlyHarvest(userId: Int, monthPrefix: String): Flow<Float?> =
        rainfallEntryDao.getMonthlyHarvest(userId, monthPrefix)

    suspend fun deleteAllEntriesForUser(userId: Int) = rainfallEntryDao.deleteAllEntriesForUser(userId)
    fun getEntryCount(userId: Int): Flow<Int> = rainfallEntryDao.getEntryCount(userId)
}
