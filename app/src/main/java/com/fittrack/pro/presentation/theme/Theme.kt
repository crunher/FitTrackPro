package com.fittrack.pro.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Background,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = TextPrimary,
    
    secondary = Secondary,
    onSecondary = Background,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = TextPrimary,
    
    tertiary = Accent,
    onTertiary = Background,
    tertiaryContainer = AccentDark,
    onTertiaryContainer = TextPrimary,
    
    background = Background,
    onBackground = TextPrimary,
    
    surface = BackgroundSurface,
    onSurface = TextPrimary,
    
    surfaceVariant = BackgroundCard,
    onSurfaceVariant = TextSecondary,
    
    outline = TextTertiary,
    outlineVariant = BackgroundElevated,
    
    error = SetFailure,
    onError = TextPrimary,
)

@Composable
fun FitTrackProTheme(
    darkTheme: Boolean = true, // Always dark
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = Background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
