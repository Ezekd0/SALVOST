package com.example.ai_ctdrs.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Night = Color(0xFF030910)
val Panel = Color(0xFF0D1723)
val Violet = Color(0xFF7044FF)
val Muted = Color(0xFF9BA9BE)
val Blue = Color(0xFF398BFF)
val Green = Color(0xFF40D78C)
val Amber = Color(0xFFFFB348)
val Red = Color(0xFFFF515E)
@Composable
fun AICTDRSTheme(darkTheme: Boolean = true, dynamicColor: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(primary = Violet, onPrimary = Color.White,
        secondary = Blue, background = Night, surface = Panel, surfaceVariant = Color(0xFF162237),
        onBackground = Color(0xFFF5F6FC), onSurface = Color(0xFFF5F6FC), onSurfaceVariant = Muted,
        outline = Color(0xFF273249), error = Red), typography = Typography, content = content)
}
