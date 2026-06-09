package com.donor.calendar.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DonorColorScheme = lightColorScheme(
    primary          = Color(0xFFB71C1C),
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFFFCDD2),
    onPrimaryContainer = Color(0xFF410002),
    secondary        = Color(0xFF1565C0),
    onSecondary      = Color.White,
    secondaryContainer = Color(0xFFD6E4FF),
    onSecondaryContainer = Color(0xFF001B3F),
    surface          = Color(0xFFFFFBFE),
    onSurface        = Color(0xFF1C1B1F),
    background       = Color(0xFFF5F5F5),
    onBackground     = Color(0xFF1C1B1F),
    error            = Color(0xFFB3261E),
    onError          = Color.White,
    errorContainer   = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
)

@Composable
fun DonorCalendarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DonorColorScheme,
        typography = Typography(),
        content = content
    )
}
