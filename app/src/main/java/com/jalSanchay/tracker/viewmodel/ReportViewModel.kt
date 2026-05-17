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
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: JalRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val userId: StateFlow<Int?> = userPreferences.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _monthEntries = MutableStateFlow<List<RainfallEntry>>(emptyList())
    val monthEntries: StateFlow<List<RainfallEntry>> = _monthEntries.asStateFlow()

    private val _prevMonthEntries = MutableStateFlow<List<RainfallEntry>>(emptyList())
    val prevMonthEntries: StateFlow<List<RainfallEntry>> = _prevMonthEntries.asStateFlow()

    fun loadMonth(uid: Int) {
        val ym = _currentMonth.value
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val from = ym.atDay(1).format(formatter)
        val to = ym.atEndOfMonth().format(formatter)

        viewModelScope.launch(Dispatchers.IO) {
            repository.getEntriesByDateRange(uid, from, to).collect {
                _monthEntries.value = it
            }
        }

        val prevYm = ym.minusMonths(1)
        val prevFrom = prevYm.atDay(1).format(formatter)
        val prevTo = prevYm.atEndOfMonth().format(formatter)

        viewModelScope.launch(Dispatchers.IO) {
            repository.getEntriesByDateRange(uid, prevFrom, prevTo).collect {
                _prevMonthEntries.value = it
            }
        }
    }

    fun previousMonth(uid: Int) {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
        loadMonth(uid)
    }

    fun nextMonth(uid: Int) {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
        loadMonth(uid)
    }

    fun generateShareText(): String {
        val ym = _currentMonth.value
        val total = _monthEntries.value.sumOf { it.litersHarvested.toDouble() }
        val monthName = ym.month.name.lowercase().replaceFirstChar { it.uppercase() }
        return "This $monthName ${ym.year}, I harvested ${"%.1f".format(total)} liters of rainwater using Jal-Sanchay Tracker! 💧"
    }

    fun generateCsv(): String {
        val sb = StringBuilder()
        sb.appendLine("Date,Rainfall (mm),Liters Harvested,Tank Level (L),Source,Notes")
        _monthEntries.value.forEach { e ->
            sb.appendLine("${e.date},${e.rainfallMm},${e.litersHarvested},${e.tankLevelAfterLiters},${e.source},\"${e.notes}\"")
        }
        return sb.toString()
    }
}
