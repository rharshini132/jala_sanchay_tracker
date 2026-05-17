package com.jalSanchay.tracker.di

import android.content.Context
import com.jalSanchay.tracker.data.db.AppDatabase
import com.jalSanchay.tracker.data.db.RainfallEntryDao
import com.jalSanchay.tracker.data.db.TankSetupDao
import com.jalSanchay.tracker.data.db.UserDao
import com.jalSanchay.tracker.data.datastore.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideTankSetupDao(db: AppDatabase): TankSetupDao = db.tankSetupDao()

    @Provides
    fun provideRainfallEntryDao(db: AppDatabase): RainfallEntryDao = db.rainfallEntryDao()

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}
