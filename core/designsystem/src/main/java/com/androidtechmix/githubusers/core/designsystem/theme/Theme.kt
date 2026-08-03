package com.androidtechmix.githubusers.core.designsystem.theme

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

private val TealPrimary = Color(0xFF0F766E)
private val TealOnPrimary = Color(0xFFFFFFFF)
private val TealContainer = Color(0xFFCCFBF1)
private val TealOnContainer = Color(0xFF134E4A)
private val SlateBackground = Color(0xFFF8FAFC)
private val SlateSurface = Color(0xFFFFFFFF)
private val SlateOnSurface = Color(0xFF0F172A)
private val SlateSecondary = Color(0xFF475569)
private val Accent = Color(0xFFEA580C)

private val DarkTealPrimary = Color(0xFF2DD4BF)
private val DarkBackground = Color(0xFF0B1220)
private val DarkSurface = Color(0xFF111827)
private val DarkOnSurface = Color(0xFFE2E8F0)

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    primaryContainer = TealContainer,
    onPrimaryContainer = TealOnContainer,
    secondary = SlateSecondary,
    onSecondary = Color.White,
    tertiary = Accent,
    onTertiary = Color.White,
    background = SlateBackground,
    onBackground = SlateOnSurface,
    surface = SlateSurface,
    onSurface = SlateOnSurface,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFF94A3B8),
)

private val DarkColors = darkColorScheme(
    primary = DarkTealPrimary,
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF115E59),
    onPrimaryContainer = Color(0xFFCCFBF1),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    tertiary = Color(0xFFFB923C),
    onTertiary = Color(0xFF431407),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF64748B),
)

@Composable
fun AndroidTechMixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AtmTypography,
        content = content,
    )
}
