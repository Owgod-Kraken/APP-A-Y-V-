package com.musicvideoplayer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.musicvideoplayer.data.model.AppThemeColor
import com.musicvideoplayer.data.model.ThemeMode

private fun lightColorSchemeForTheme(themeColor: AppThemeColor) = when (themeColor) {
    AppThemeColor.BLUE -> lightColorScheme(
        primary = Blue40, onPrimary = Color.White,
        primaryContainer = Blue90, onPrimaryContainer = Blue10,
        secondary = Blue30, onSecondary = Color.White,
        secondaryContainer = Blue80, surface = Color(0xFFFCFCFF),
        surfaceVariant = Color(0xFFE1E2EC)
    )
    AppThemeColor.RED -> lightColorScheme(
        primary = Red40, onPrimary = Color.White,
        primaryContainer = Red90, onPrimaryContainer = Red10,
        secondary = Red30, onSecondary = Color.White,
        secondaryContainer = Red80, surface = Color(0xFFFCFCFF),
        surfaceVariant = Color(0xFFE1E2EC)
    )
    AppThemeColor.GREEN -> lightColorScheme(
        primary = Green40, onPrimary = Color.White,
        primaryContainer = Green90, onPrimaryContainer = Green10,
        secondary = Green30, onSecondary = Color.White,
        secondaryContainer = Green80, surface = Color(0xFFFCFCFF),
        surfaceVariant = Color(0xFFE1E2EC)
    )
    AppThemeColor.PURPLE -> lightColorScheme(
        primary = Purple40, onPrimary = Color.White,
        primaryContainer = Purple90, onPrimaryContainer = Purple10,
        secondary = Purple30, onSecondary = Color.White,
        secondaryContainer = Purple80, surface = Color(0xFFFCFCFF),
        surfaceVariant = Color(0xFFE1E2EC)
    )
    AppThemeColor.ORANGE -> lightColorScheme(
        primary = Orange40, onPrimary = Color.White,
        primaryContainer = Orange90, onPrimaryContainer = Orange10,
        secondary = Orange30, onSecondary = Color.White,
        secondaryContainer = Orange80, surface = Color(0xFFFCFCFF),
        surfaceVariant = Color(0xFFE1E2EC)
    )
    AppThemeColor.TEAL -> lightColorScheme(
        primary = Teal40, onPrimary = Color.White,
        primaryContainer = Teal90, onPrimaryContainer = Teal10,
        secondary = Teal30, onSecondary = Color.White,
        secondaryContainer = Teal80, surface = Color(0xFFFCFCFF),
        surfaceVariant = Color(0xFFE1E2EC)
    )
    AppThemeColor.PINK -> lightColorScheme(
        primary = Pink40, onPrimary = Color.White,
        primaryContainer = Pink90, onPrimaryContainer = Pink10,
        secondary = Pink30, onSecondary = Color.White,
        secondaryContainer = Pink80, surface = Color(0xFFFCFCFF),
        surfaceVariant = Color(0xFFE1E2EC)
    )
    AppThemeColor.YELLOW -> lightColorScheme(
        primary = Yellow40, onPrimary = Color.White,
        primaryContainer = Yellow90, onPrimaryContainer = Yellow10,
        secondary = Yellow30, onSecondary = Color.White,
        secondaryContainer = Yellow80, surface = Color(0xFFFCFCFF),
        surfaceVariant = Color(0xFFE1E2EC)
    )
}

private fun darkColorSchemeForTheme(themeColor: AppThemeColor) = when (themeColor) {
    AppThemeColor.BLUE -> darkColorScheme(
        primary = Blue80, onPrimary = Blue20,
        primaryContainer = Blue30, onPrimaryContainer = Blue90,
        secondary = Blue80, onSecondary = Blue20,
        secondaryContainer = Blue30, surface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFF44474F)
    )
    AppThemeColor.RED -> darkColorScheme(
        primary = Red80, onPrimary = Red20,
        primaryContainer = Red30, onPrimaryContainer = Red90,
        secondary = Red80, onSecondary = Red20,
        secondaryContainer = Red30, surface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFF44474F)
    )
    AppThemeColor.GREEN -> darkColorScheme(
        primary = Green80, onPrimary = Green20,
        primaryContainer = Green30, onPrimaryContainer = Green90,
        secondary = Green80, onSecondary = Green20,
        secondaryContainer = Green30, surface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFF44474F)
    )
    AppThemeColor.PURPLE -> darkColorScheme(
        primary = Purple80, onPrimary = Purple20,
        primaryContainer = Purple30, onPrimaryContainer = Purple90,
        secondary = Purple80, onSecondary = Purple20,
        secondaryContainer = Purple30, surface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFF44474F)
    )
    AppThemeColor.ORANGE -> darkColorScheme(
        primary = Orange80, onPrimary = Orange20,
        primaryContainer = Orange30, onPrimaryContainer = Orange90,
        secondary = Orange80, onSecondary = Orange20,
        secondaryContainer = Orange30, surface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFF44474F)
    )
    AppThemeColor.TEAL -> darkColorScheme(
        primary = Teal80, onPrimary = Teal20,
        primaryContainer = Teal30, onPrimaryContainer = Teal90,
        secondary = Teal80, onSecondary = Teal20,
        secondaryContainer = Teal30, surface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFF44474F)
    )
    AppThemeColor.PINK -> darkColorScheme(
        primary = Pink80, onPrimary = Pink20,
        primaryContainer = Pink30, onPrimaryContainer = Pink90,
        secondary = Pink80, onSecondary = Pink20,
        secondaryContainer = Pink30, surface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFF44474F)
    )
    AppThemeColor.YELLOW -> darkColorScheme(
        primary = Yellow80, onPrimary = Yellow20,
        primaryContainer = Yellow30, onPrimaryContainer = Yellow90,
        secondary = Yellow80, onSecondary = Yellow20,
        secondaryContainer = Yellow30, surface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFF44474F)
    )
}

@Composable
fun MusicVideoPlayerTheme(
    themeColor: AppThemeColor = AppThemeColor.BLUE,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme = if (isDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try { dynamicDarkColorScheme(context) } catch (_: Exception) { darkColorSchemeForTheme(themeColor) }
        } else {
            darkColorSchemeForTheme(themeColor)
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try { dynamicLightColorScheme(context) } catch (_: Exception) { lightColorSchemeForTheme(themeColor) }
        } else {
            lightColorSchemeForTheme(themeColor)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
