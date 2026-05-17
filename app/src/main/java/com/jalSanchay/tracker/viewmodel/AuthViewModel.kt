package com.jalSanchay.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalSanchay.tracker.data.datastore.UserPreferences
import com.jalSanchay.tracker.data.db.AppDatabase
import com.jalSanchay.tracker.data.repository.JalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: Int, val hasSetup: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: JalRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val sessionUserId: StateFlow<Int?> = userPreferences.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _sessionChecked = MutableStateFlow(false)
    val sessionChecked: StateFlow<Boolean> = _sessionChecked.asStateFlow()

    init {
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.userId.first().let { userId ->
                if (userId != null && userId > 0) {
                    val user = repository.getUserById(userId)
                    if (user != null) {
                        val setup = repository.getSetupByUser(userId)
                        _authState.value = AuthState.Success(userId, setup != null)
                    }
                }
                _sessionChecked.value = true
            }
        }
    }

    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = repository.getUserByEmail(email.trim())
                if (user == null) {
                    _authState.value = AuthState.Error("Invalid email or password")
                    return@launch
                }
                val hash = AppDatabase.hashPassword(password)
                if (user.passwordHash != hash) {
                    _authState.value = AuthState.Error("Invalid email or password")
                    return@launch
                }
                userPreferences.saveUserId(user.id)
                val setup = repository.getSetupByUser(user.id)
                _authState.value = AuthState.Success(user.id, setup != null)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun registerUser(
        name: String,
        email: String,
        password: String,
        city: String,
        householdSize: String
    ) {
        _authState.value = AuthState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existing = repository.getUserByEmail(email.trim())
                if (existing != null) {
                    _authState.value = AuthState.Error("Email already registered")
                    return@launch
                }
                val hash = AppDatabase.hashPassword(password)
                val user = com.jalSanchay.tracker.data.model.User(
                    name = name.trim(),
                    email = email.trim(),
                    passwordHash = hash,
                    city = city.trim(),
                    householdSize = householdSize
                )
                val userId = repository.insertUser(user).toInt()
                userPreferences.saveUserId(userId)
                _authState.value = AuthState.Success(userId, false)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Registration failed: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.clearSession()
            _authState.value = AuthState.Idle
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
