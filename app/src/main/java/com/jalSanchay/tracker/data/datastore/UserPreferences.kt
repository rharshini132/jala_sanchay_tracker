package com.jalSanchay.tracker.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jal_sanchay_prefs")

@Singleton
class UserPreferences @Inject constructor(private val context: Context) {

    companion object {
        val USER_ID_KEY = intPreferencesKey("user_id")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val WATER_RATE_KEY = floatPreferencesKey("water_rate")
        val DAILY_NEED_KEY = floatPreferencesKey("daily_need")
        val FONT_SIZE_KEY = stringPreferencesKey("font_size")
        val DAILY_REMINDER_KEY = booleanPreferencesKey("daily_reminder")
        val REMINDER_TIME_KEY = stringPreferencesKey("reminder_time")
        val RAIN_ALERT_THRESHOLD_KEY = intPreferencesKey("rain_alert_threshold")
        val UNITS_KEY = stringPreferencesKey("units")
        val COLOR_THEME_KEY = stringPreferencesKey("color_theme")
    }

    val userId: Flow<Int?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY]
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY] ?: false
    }

    val waterRate: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[WATER_RATE_KEY] ?: 0.05f
    }

    val dailyNeed: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[DAILY_NEED_KEY] ?: 135f
    }

    val fontSize: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[FONT_SIZE_KEY] ?: "Medium"
    }

    val dailyReminder: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DAILY_REMINDER_KEY] ?: false
    }

    val reminderTime: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[REMINDER_TIME_KEY] ?: "08:00"
    }

    val rainAlertThreshold: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[RAIN_ALERT_THRESHOLD_KEY] ?: 80
    }

    val units: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[UNITS_KEY] ?: "Metric"
    }

    val colorTheme: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[COLOR_THEME_KEY] ?: "Blue"
    }

    suspend fun saveUserId(id: Int) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = id
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun setWaterRate(rate: Float) {
        context.dataStore.edit { prefs ->
            prefs[WATER_RATE_KEY] = rate
        }
    }

    suspend fun setDailyNeed(need: Float) {
        context.dataStore.edit { prefs ->
            prefs[DAILY_NEED_KEY] = need
        }
    }

    suspend fun setFontSize(size: String) {
        context.dataStore.edit { prefs ->
            prefs[FONT_SIZE_KEY] = size
        }
    }

    suspend fun setDailyReminder(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DAILY_REMINDER_KEY] = enabled
        }
    }

    suspend fun setReminderTime(time: String) {
        context.dataStore.edit { prefs ->
            prefs[REMINDER_TIME_KEY] = time
        }
    }

    suspend fun setRainAlertThreshold(threshold: Int) {
        context.dataStore.edit { prefs ->
            prefs[RAIN_ALERT_THRESHOLD_KEY] = threshold
        }
    }

    suspend fun setUnits(unit: String) {
        context.dataStore.edit { prefs ->
            prefs[UNITS_KEY] = unit
        }
    }

    suspend fun setColorTheme(theme: String) {
        context.dataStore.edit { prefs ->
            prefs[COLOR_THEME_KEY] = theme
        }
    }
}
