package com.jalSanchay.tracker.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Setup : Screen("setup")
    object Dashboard : Screen("dashboard")
    object LogRainfall : Screen("log_rainfall")
    object History : Screen("history")
    object MonthlyReport : Screen("monthly_report")
    object Impact : Screen("impact")
    object Tips : Screen("tips")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
