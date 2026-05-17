package com.jalSanchay.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalSanchay.tracker.data.datastore.UserPreferences
import com.jalSanchay.tracker.data.model.TankSetup
import com.jalSanchay.tracker.data.repository.JalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SetupState {
    object Idle : SetupState()
    object Loading : SetupState()
    object Success : SetupState()
    data class Error(val message: String) : SetupState()
}

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val repository: JalRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _setupState = MutableStateFlow<SetupState>(SetupState.Idle)
    val setupState: StateFlow<SetupState> = _setupState.asStateFlow()

    private val _existingSetup = MutableStateFlow<TankSetup?>(null)
    val existingSetup: StateFlow<TankSetup?> = _existingSetup.asStateFlow()

    val userId: StateFlow<Int?> = userPreferences.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun loadExistingSetup(uid: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val setup = repository.getSetupByUser(uid)
            _existingSetup.value = setup
        }
    }

    fun saveSetup(
        userId: Int,
        roofArea: Float,
        roofMaterial: String,
        runoffCoefficient: Float,
        tankCapacity: Float,
        tankMaterial: String,
        currentWaterLevel: Float
    ) {
        _setupState.value = SetupState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existing = repository.getSetupByUser(userId)
                val setup = TankSetup(
                    id = existing?.id ?: 0,
                    userId = userId,
                    roofAreaM2 = roofArea,
                    roofMaterial = roofMaterial,
                    runoffCoefficient = runoffCoefficient,
                    tankCapacityLiters = tankCapacity,
                    tankMaterial = tankMaterial,
                    currentWaterLevelLiters = currentWaterLevel,
                    updatedAt = System.currentTimeMillis()
                )
                repository.insertOrUpdateSetup(setup)
                _setupState.value = SetupState.Success
            } catch (e: Exception) {
                _setupState.value = SetupState.Error("Failed to save: ${e.message}")
            }
        }
    }

    fun resetState() {
        _setupState.value = SetupState.Idle
    }
}
