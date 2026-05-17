package com.jalSanchay.tracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = Error,
    onPrimary = OnSurfaceDark,
    onSecondary = OnSurfaceDark,
    onTertiary = OnSurfaceDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    onError = OnSurfaceDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = OnBackgroundDark,
    outline = GlassBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = BackgroundLight,
    surface = SurfaceLight,
    error = Error,
    onPrimary = OnSurfaceLight,
    onSecondary = OnSurfaceDark,
    onTertiary = OnSurfaceLight,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight,
    onError = OnSurfaceDark,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = OnBackgroundLight,
    outline = GlassBorderLight
)

@Composable
fun JalSanchayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JalTypography,
        content = content
    )
}
