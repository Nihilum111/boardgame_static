package com.example.boardgamestatistic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = White,
    primaryContainer = Blue700,
    onPrimaryContainer = White,
    secondary = Teal200,
    onSecondary = Black,
    // Добавьте другие цвета по необходимости
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue200,
    onPrimary = Black,
    primaryContainer = Blue700,
    onPrimaryContainer = White,
    secondary = Teal200,
    onSecondary = Black,
    // Добавьте другие цвета по необходимости
)

@Composable
fun BoardGameStatisticTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}