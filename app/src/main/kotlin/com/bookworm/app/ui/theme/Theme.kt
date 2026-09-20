package com.bookworm.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── Colour palette (matches style.css :root variables) ───────────────────────
val BgColor        = Color(0xFF0D0D0D)
val SurfaceColor   = Color(0xFF111111)
val Surface2Color  = Color(0xFF181818)
val BorderColor    = Color(0xFF2A2A2A)
val TextColor      = Color(0xFFFFFFFF)
val MutedColor     = Color(0xFF888888)
val OrangeAccent   = Color(0xFFE8943A)
val GoldAccent     = Color(0xFFF5C842)
val GreenAccent    = Color(0xFF4CAF50)
val RedAccent      = Color(0xFFE74C3C)
val BlueAccent     = Color(0xFF3498DB)
val PurpleAccent   = Color(0xFF9B59B6)
val NavBarColor    = Color(0xFF0A0A0A)
val CardColor      = Color(0xFF161616)

// ─── Dark colour scheme ────────────────────────────────────────────────────────
private val darkScheme = darkColorScheme(
    primary            = OrangeAccent,
    onPrimary          = TextColor,
    primaryContainer   = Surface2Color,
    onPrimaryContainer = TextColor,
    secondary          = GoldAccent,
    onSecondary        = BgColor,
    background         = BgColor,
    onBackground       = TextColor,
    surface            = SurfaceColor,
    onSurface          = TextColor,
    surfaceVariant     = Surface2Color,
    onSurfaceVariant   = MutedColor,
    outline            = BorderColor,
    error              = RedAccent,
    onError            = TextColor,
)

// ─── Typography ───────────────────────────────────────────────────────────────
private val BookWormTypography = Typography(
    headlineLarge  = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = TextColor),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold,      fontSize = 20.sp, color = TextColor),
    headlineSmall  = TextStyle(fontWeight = FontWeight.Bold,      fontSize = 18.sp, color = TextColor),
    titleLarge     = TextStyle(fontWeight = FontWeight.Bold,      fontSize = 16.sp, color = TextColor),
    titleMedium    = TextStyle(fontWeight = FontWeight.SemiBold,  fontSize = 14.sp, color = TextColor),
    titleSmall     = TextStyle(fontWeight = FontWeight.Medium,    fontSize = 13.sp, color = TextColor),
    bodyLarge      = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 14.sp, color = TextColor),
    bodyMedium     = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 13.sp, color = TextColor),
    bodySmall      = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 12.sp, color = MutedColor),
    labelSmall     = TextStyle(fontWeight = FontWeight.Bold,      fontSize = 10.sp, color = MutedColor),
)

// ─── Theme entry point ────────────────────────────────────────────────────────
@Composable
fun BookWormTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkScheme,
        typography  = BookWormTypography,
        content     = content
    )
}
