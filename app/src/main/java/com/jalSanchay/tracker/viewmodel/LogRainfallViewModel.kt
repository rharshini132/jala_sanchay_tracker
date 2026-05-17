package com.jalSanchay.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalSanchay.tracker.data.datastore.UserPreferences
import com.jalSanchay.tracker.data.model.RainfallEntry
import com.jalSanchay.tracker.data.model.TankSetup
import com.jalSanchay.tracker.data.repository.JalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LogRainfallState {
    object Idle : LogRainfallState()
    object Loading : LogRainfallState()
    data class Success(val litersHarvested: Float) : LogRainfallState()
    data class Error(val message: String) : LogRainfallState()
}

@HiltViewModel
class LogRainfallViewModel @Inject constructor(
    private val repository: JalRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _logState = MutableStateFlow<LogRainfallState>(LogRainfallState.Idle)
    val logState: StateFlow<LogRainfallState> = _logState.asStateFlow()

    private val _tankSetup = MutableStateFlow<TankSetup?>(null)
    val tankSetup: StateFlow<TankSetup?> = _tankSetup.asStateFlow()

    val userId: StateFlow<Int?> = userPreferences.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun loadSetup(uid: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _tankSetup.value = repository.getSetupByUser(uid)
        }
    }

    fun calculatePreview(rainfallMm: Float): Float {
        val setup = _tankSetup.value ?: return 0f
        return repository.calculateHarvest(setup.roofAreaM2, rainfallMm, setup.runoffCoefficient)
    }

    fun saveEntry(
        userId: Int,
        date: String,
        rainfallMm: Float,
        source: String,
        notes: String,
        updateTankLevel: Boolean
    ) {
        if (rainfallMm <= 0f) {
            _logState.value = LogRainfallState.Error("Rainfall must be greater than 0")
            return
        }
        val setup = _tankSetup.value
        if (setup == null) {
            _logState.value = LogRainfallState.Error("Please complete tank setup first")
            return
        }

        _logState.value = LogRainfallState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val liters = repository.calculateHarvest(setup.roofAreaM2, rainfallMm, setup.runoffCoefficient)
                val newTankLevel = if (updateTankLevel) {
                    minOf(setup.currentWaterLevelLiters + liters, setup.tankCapacityLiters)
                } else {
                    setup.currentWaterLevelLiters
                }

                val entry = RainfallEntry(
                    userId = userId,
                    date = date,
                    rainfallMm = rainfallMm,
                    litersHarvested = liters,
                    tankLevelAfterLiters = newTankLevel,
                    source = source,
                    notes = notes,
                    roofAreaUsed = setup.roofAreaM2,
                    runoffUsed = setup.runoffCoefficient
                )
                repository.insertEntry(entry)

                if (updateTankLevel) {
                    repository.updateWaterLevel(userId, newTankLevel)
                    _tankSetup.value = setup.copy(currentWaterLevelLiters = newTankLevel)
                }

                _logState.value = LogRainfallState.Success(liters)
            } catch (e: Exception) {
                _logState.value = LogRainfallState.Error("Failed to save: ${e.message}")
            }
        }
    }

    fun resetState() {
        _logState.value = LogRainfallState.Idle
    }
}
