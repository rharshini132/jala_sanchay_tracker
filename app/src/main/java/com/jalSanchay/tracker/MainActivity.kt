package com.jalSanchay.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.jalSanchay.tracker.ui.navigation.NavGraph
import com.jalSanchay.tracker.ui.navigation.Screen
import com.jalSanchay.tracker.ui.theme.JalSanchayTheme
import com.jalSanchay.tracker.data.datastore.UserPreferences
import com.jalSanchay.tracker.viewmodel.AuthState
import com.jalSanchay.tracker.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkMode by userPreferences.darkMode.collectAsState(initial = false)
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.authState.collectAsState()
            val sessionChecked by authViewModel.sessionChecked.collectAsState()

            JalSanchayTheme(darkTheme = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (sessionChecked) {
                        val startDestination = when (authState) {
                            is AuthState.Success -> {
                                val success = authState as AuthState.Success
                                if (success.hasSetup) Screen.Dashboard.route else Screen.Setup.route
                            }
                            else -> Screen.Login.route
                        }

                        val navController = rememberNavController()
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}
