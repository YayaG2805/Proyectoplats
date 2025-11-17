package com.example.proyecto.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================
// COLORES PERSONALIZADOS PIGGYMOBILE
// ============================================

// Primarios - Rosa/Fucsia (tema cochinito)
private val PiggyPink = Color(0xFFE91E63)
private val PiggyPinkLight = Color(0xFFF48FB1)
private val PiggyPinkDark = Color(0xFFC2185B)
private val PiggyPinkVeryLight = Color(0xFFFCE4EC)

// Secundarios - Verde (ahorro)
private val PiggyGreen = Color(0xFF4CAF50)
private val PiggyGreenLight = Color(0xFF81C784)
private val PiggyGreenDark = Color(0xFF388E3C)

// Terciarios - Naranja (alertas)
private val PiggyOrange = Color(0xFFFF9800)
private val PiggyOrangeLight = Color(0xFFFFB74D)
private val PiggyOrangeDark = Color(0xFFF57C00)

// ============================================
// ESQUEMA OSCURO - OPTIMIZADO
// ============================================
private val DarkColorScheme = darkColorScheme(
    // === PRIMARIOS (Rosa) ===
    primary = PiggyPinkLight,
    onPrimary = Color(0xFF1C1B1F),
    primaryContainer = Color(0xFF4A3A4A),
    onPrimaryContainer = Color(0xFFFFD9E3),

    // === SECUNDARIOS (Verde - Ahorro) ===
    secondary = PiggyGreenLight,
    onSecondary = Color(0xFF1C1B1F),
    secondaryContainer = Color(0xFF2E3E2E),
    onSecondaryContainer = Color(0xFFC8E6C9),

    // === TERCIARIOS (Naranja - Alertas) ===
    tertiary = PiggyOrangeLight,
    onTertiary = Color(0xFF1C1B1F),
    tertiaryContainer = Color(0xFF3E3E2E),
    onTertiaryContainer = Color(0xFFFFE0B2),

    // === FONDO ===
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),

    // === SUPERFICIE ===
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    surfaceTint = PiggyPinkLight,

    // === SUPERFICIE CONTAINER ===
    surfaceContainer = Color(0xFF211F26),
    surfaceContainerHigh = Color(0xFF2B2930),
    surfaceContainerHighest = Color(0xFF36343B),
    surfaceContainerLow = Color(0xFF1D1B20),
    surfaceContainerLowest = Color(0xFF0F0D13),

    // === ERROR ===
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // === OUTLINE ===
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),

    // === INVERSE ===
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF1C1B1F),
    inversePrimary = PiggyPink,

    // === SCRIM ===
    scrim = Color(0xFF000000)
)

// ============================================
// ESQUEMA CLARO - OPTIMIZADO
// ============================================
private val LightColorScheme = lightColorScheme(
    // === PRIMARIOS (Rosa) ===
    primary = PiggyPink,
    onPrimary = Color.White,
    primaryContainer = PiggyPinkVeryLight,
    onPrimaryContainer = PiggyPinkDark,

    // === SECUNDARIOS (Verde - Ahorro) ===
    secondary = PiggyGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = PiggyGreenDark,

    // === TERCIARIOS (Naranja - Alertas) ===
    tertiary = PiggyOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = PiggyOrangeDark,

    // === FONDO ===
    background = Color(0xFFFEFBFF),
    onBackground = Color(0xFF1C1B1F),

    // === SUPERFICIE ===
    surface = Color(0xFFFEFBFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    surfaceTint = PiggyPink,

    // === SUPERFICIE CONTAINER ===
    surfaceContainer = Color(0xFFF3EDF7),
    surfaceContainerHigh = Color(0xFFECE6F0),
    surfaceContainerHighest = Color(0xFFE6E0E9),
    surfaceContainerLow = Color(0xFFF7F2FA),
    surfaceContainerLowest = Color.White,

    // === ERROR ===
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // === OUTLINE ===
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),

    // === INVERSE ===
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = PiggyPinkLight,

    // === SCRIM ===
    scrim = Color(0xFF000000)
)

@Composable
fun ProyectoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color solo en Android 12+ pero desactivado por defecto
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Establecer color de barra de estado según el tema
            window.statusBarColor = colorScheme.surface.toArgb()

            // Establecer color de barra de navegación
            window.navigationBarColor = colorScheme.surface.toArgb()

            // Controlar iconos de sistema (claro/oscuro)
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}