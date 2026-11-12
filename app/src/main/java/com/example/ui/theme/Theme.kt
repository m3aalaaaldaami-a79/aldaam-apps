package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = EmeraldDeepBackground,
    primaryContainer = EmeraldPrimary,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = EmeraldDeepBackground,
    secondaryContainer = EmeraldSurfaceVariant,
    onSecondaryContainer = TextOnDarkPrimary,
    tertiary = GoldMuted,
    onTertiary = EmeraldDeepBackground,
    background = EmeraldDeepBackground,
    onBackground = TextOnDarkPrimary,
    surface = EmeraldSurface,
    onSurface = TextOnDarkPrimary,
    surfaceVariant = EmeraldSurfaceVariant,
    onSurfaceVariant = TextOnDarkSecondary,
    outline = EmeraldBorder,
    outlineVariant = EmeraldPrimary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SebhaTypography,
        content = content
    )
}
