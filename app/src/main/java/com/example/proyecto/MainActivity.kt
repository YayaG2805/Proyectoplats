package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.proyecto.notifications.NotificationHelper
import com.example.proyecto.presentation.navigation.AppNavHost

/**
 * Activity principal de PiggyMobile.
 *
 * Maneja la navegación inicial y el deep linking desde notificaciones.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si viene desde notificación
        val shouldNavigateToDailyExpense = intent?.getBooleanExtra(
            NotificationHelper.EXTRA_NAVIGATE_TO_DAILY_EXPENSE,
            false
        ) ?: false

        setContent {
            MaterialTheme {
                AppNavHost(startWithDailyExpense = shouldNavigateToDailyExpense)
            }
        }
    }
}