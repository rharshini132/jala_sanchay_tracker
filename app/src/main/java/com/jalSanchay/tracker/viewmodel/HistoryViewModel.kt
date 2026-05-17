package com.jalSanchay.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalSanchay.tracker.data.datastore.UserPreferences
import com.jalSanchay.tracker.data.model.RainfallEntry
import com.jalSanchay.tracker.data.repository.JalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: JalRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val userId: StateFlow<Int?> = userPreferences.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _entries = MutableStateFlow<List<RainfallEntry>>(emptyList())
    val entries: StateFlow<List<RainfallEntry>> = _entries.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    fun loadEntries(uid: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllEntries(uid).collect { list ->
                _entries.value = list
            }
        }
    }

    fun applyFilter(uid: Int, filter: String, customFrom: String? = null, customTo: String? = null) {
        _selectedFilter.value = filter
        val now = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        viewModelScope.launch(Dispatchers.IO) {
            when (filter) {
                "All" -> {
                    repository.getAllEntries(uid).collect { _entries.value = it }
                }
                "This Week" -> {
                    val from = now.minusDays(7).format(formatter)
                    val to = now.format(formatter)
                    repository.getEntriesByDateRange(uid, from, to).collect { _entries.value = it }
                }
                "This Month" -> {
                    val from = now.withDayOfMonth(1).format(formatter)
                    val to = now.format(formatter)
                    repository.getEntriesByDateRange(uid, from, to).collect { _entries.value = it }
                }
                "Last 3 Months" -> {
                    val from = now.minusMonths(3).format(formatter)
                    val to = now.format(formatter)
                    repository.getEntriesByDateRange(uid, from, to).collect { _entries.value = it }
                }
                "Custom Range" -> {
                    if (customFrom != null && customTo != null) {
                        repository.getEntriesByDateRange(uid, customFrom, customTo).collect { _entries.value = it }
                    }
                }
            }
        }
    }

    fun deleteEntry(entry: RainfallEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEntry(entry)
        }
    }

    fun updateEntry(entry: RainfallEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEntry(entry)
        }
    }
}
