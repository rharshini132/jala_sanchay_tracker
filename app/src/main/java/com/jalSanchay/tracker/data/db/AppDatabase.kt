package com.jalSanchay.tracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jalSanchay.tracker.data.model.RainfallEntry
import com.jalSanchay.tracker.data.model.TankSetup
import com.jalSanchay.tracker.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Database(
    entities = [User::class, TankSetup::class, RainfallEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tankSetupDao(): TankSetupDao
    abstract fun rainfallEntryDao(): RainfallEntryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jal_sanchay_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val userDao = database.userDao()
                    val tankDao = database.tankSetupDao()
                    val entryDao = database.rainfallEntryDao()

                    // Seed user
                    val passwordHash = hashPassword("water123")
                    val userId = userDao.insertUser(
                        User(
                            name = "Priya Sharma",
                            email = "priya@jalsanchay.com",
                            passwordHash = passwordHash,
                            city = "Pune",
                            householdSize = "3-4 members",
                            createdAt = System.currentTimeMillis()
                        )
                    ).toInt()

                    // Seed tank setup
                    tankDao.insertOrUpdate(
                        TankSetup(
                            userId = userId,
                            roofAreaM2 = 80f,
                            roofMaterial = "Concrete/Tile",
                            runoffCoefficient = 0.85f,
                            tankCapacityLiters = 5000f,
                            tankMaterial = "Concrete",
                            currentWaterLevelLiters = 1200f,
                            updatedAt = System.currentTimeMillis()
                        )
                    )

                    // Seed rainfall entries
                    val roofArea = 80f
                    val runoff = 0.85f
                    val entries = listOf(
                        RainfallEntry(userId = userId, date = "2026-01-08", rainfallMm = 12f, litersHarvested = 95.6f, tankLevelAfterLiters = 95.6f, source = "Manual Measurement", notes = "Light morning rain", roofAreaUsed = roofArea, runoffUsed = runoff),
                        RainfallEntry(userId = userId, date = "2026-01-15", rainfallMm = 5f, litersHarvested = 39.8f, tankLevelAfterLiters = 135.4f, source = "Weather App Reading", notes = "Drizzle afternoon", roofAreaUsed = roofArea, runoffUsed = runoff),
                        RainfallEntry(userId = userId, date = "2026-01-22", rainfallMm = 18f, litersHarvested = 143.4f, tankLevelAfterLiters = 278.8f, source = "Manual Measurement", notes = "Heavy downpour", roofAreaUsed = roofArea, runoffUsed = runoff),
                        RainfallEntry(userId = userId, date = "2026-02-03", rainfallMm = 7f, litersHarvested = 55.7f, tankLevelAfterLiters = 334.5f, source = "Estimated", notes = "Overnight rain", roofAreaUsed = roofArea, runoffUsed = runoff),
                        RainfallEntry(userId = userId, date = "2026-02-10", rainfallMm = 22f, litersHarvested = 175.2f, tankLevelAfterLiters = 509.7f, source = "Manual Measurement", notes = "Strong monsoon burst", roofAreaUsed = roofArea, runoffUsed = runoff),
                        RainfallEntry(userId = userId, date = "2026-02-14", rainfallMm = 9f, litersHarvested = 71.7f, tankLevelAfterLiters = 581.4f, source = "Weather App Reading", notes = "Valentine rain", roofAreaUsed = roofArea, runoffUsed = runoff),
                        RainfallEntry(userId = userId, date = "2026-02-20", rainfallMm = 15f, litersHarvested = 119.5f, tankLevelAfterLiters = 700.9f, source = "Manual Measurement", notes = "Moderate showers", roofAreaUsed = roofArea, runoffUsed = runoff),
                        RainfallEntry(userId = userId, date = "2026-02-25", rainfallMm = 11f, litersHarvested = 87.6f, tankLevelAfterLiters = 788.5f, source = "Estimated", notes = "Light evening rain", roofAreaUsed = roofArea, runoffUsed = runoff)
                    )
                    entries.forEach { entryDao.insertEntry(it) }
                }
            }
        }
    }
}
