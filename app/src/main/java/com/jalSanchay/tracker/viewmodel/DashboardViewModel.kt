package com.jalSanchay.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalSanchay.tracker.data.datastore.UserPreferences
import com.jalSanchay.tracker.data.model.RainfallEntry
import com.jalSanchay.tracker.data.model.TankSetup
import com.jalSanchay.tracker.data.model.User
import com.jalSanchay.tracker.data.repository.JalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: JalRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val userId: StateFlow<Int?> = userPreferences.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _tankSetup = MutableStateFlow<TankSetup?>(null)
    val tankSetup: StateFlow<TankSetup?> = _tankSetup.asStateFlow()

    private val _recentEntries = MutableStateFlow<List<RainfallEntry>>(emptyList())
    val recentEntries: StateFlow<List<RainfallEntry>> = _recentEntries.asStateFlow()

    private val _totalHarvest = MutableStateFlow(0f)
    val totalHarvest: StateFlow<Float> = _totalHarvest.asStateFlow()

    private val _todayHarvest = MutableStateFlow(0f)
    val todayHarvest: StateFlow<Float> = _todayHarvest.asStateFlow()

    private val _weekHarvest = MutableStateFlow(0f)
    val weekHarvest: StateFlow<Float> = _weekHarvest.asStateFlow()

    private val _monthHarvest = MutableStateFlow(0f)
    val monthHarvest: StateFlow<Float> = _monthHarvest.asStateFlow()

    val darkMode: StateFlow<Boolean> = userPreferences.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun loadDashboard(uid: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _user.value = repository.getUserById(uid)
            _tankSetup.value = repository.getSetupByUser(uid)
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.getRecentEntries(uid).collect { entries ->
                _recentEntries.value = entries
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.getTotalHarvest(uid).collect { total ->
                _totalHarvest.value = total ?: 0f
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            repository.getEntriesByDateRange(uid, today, today).collect { entries ->
                _todayHarvest.value = entries.sumOf { it.litersHarvested.toDouble() }.toFloat()
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val now = LocalDate.now()
            val weekStart = now.minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val weekEnd = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            repository.getEntriesByDateRange(uid, weekStart, weekEnd).collect { entries ->
                _weekHarvest.value = entries.sumOf { it.litersHarvested.toDouble() }.toFloat()
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val monthPrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
            repository.getMonthlyHarvest(uid, monthPrefix).collect { total ->
                _monthHarvest.value = total ?: 0f
            }
        }
    }

    fun refreshSetup(uid: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _tankSetup.value = repository.getSetupByUser(uid)
        }
    }
}
