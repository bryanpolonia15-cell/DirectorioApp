package com.bryan.directorio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Azul = Color(0xFF1F4E79)
private val AzulClaro = Color(0xFFD6E4F0)
private val Rojo = Color(0xFFC62828)

private val LightColors = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = Azul,
    secondary = Rojo,
    error = Rojo,
    background = Color(0xFFF7F9FC),
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9CC3E6),
    onPrimary = Color(0xFF0B2540),
    primaryContainer = Color(0xFF1F4E79),
    secondary = Color(0xFFEF9A9A),
    error = Color(0xFFEF9A9A)
)

@Composable
fun DirectorioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
