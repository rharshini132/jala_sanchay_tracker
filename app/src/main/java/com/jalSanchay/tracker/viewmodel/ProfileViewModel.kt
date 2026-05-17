package com.jalSanchay.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalSanchay.tracker.data.datastore.UserPreferences
import com.jalSanchay.tracker.data.model.TankSetup
import com.jalSanchay.tracker.data.model.User
import com.jalSanchay.tracker.data.repository.JalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: JalRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val userId: StateFlow<Int?> = userPreferences.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _tankSetup = MutableStateFlow<TankSetup?>(null)
    val tankSetup: StateFlow<TankSetup?> = _tankSetup.asStateFlow()

    private val _totalHarvest = MutableStateFlow(0f)
    val totalHarvest: StateFlow<Float> = _totalHarvest.asStateFlow()

    private val _totalRainfall = MutableStateFlow(0f)
    val totalRainfall: StateFlow<Float> = _totalRainfall.asStateFlow()

    private val _entryCount = MutableStateFlow(0)
    val entryCount: StateFlow<Int> = _entryCount.asStateFlow()

    val dailyNeed: StateFlow<Float> = userPreferences.dailyNeed
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 135f)

    fun loadProfile(uid: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _user.value = repository.getUserById(uid)
            _tankSetup.value = repository.getSetupByUser(uid)
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTotalHarvest(uid).collect { _totalHarvest.value = it ?: 0f }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTotalRainfall(uid).collect { _totalRainfall.value = it ?: 0f }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.getEntryCount(uid).collect { _entryCount.value = it }
        }
    }

    fun updateProfile(uid: Int, name: String, city: String, householdSize: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.getUserById(uid) ?: return@launch
            val updated = current.copy(name = name, city = city, householdSize = householdSize)
            repository.updateUser(updated)
            _user.value = updated
        }
    }

    fun clearAllHistory(uid: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllEntriesForUser(uid)
        }
    }

    fun deleteAccount(uid: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllEntriesForUser(uid)
            repository.deleteSetupByUser(uid)
            val user = repository.getUserById(uid)
            if (user != null) repository.deleteUser(user)
            userPreferences.clearSession()
        }
    }
}
