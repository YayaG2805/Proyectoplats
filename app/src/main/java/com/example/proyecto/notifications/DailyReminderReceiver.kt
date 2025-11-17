package com.example.proyecto.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.proyecto.data.local.DailyExpenseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * BroadcastReceiver que maneja la alarma diaria para recordatorios de gastos.
 *
 * Se activa diariamente a las 8 PM (configurado en AlarmScheduler).
 * Verifica si el usuario ha registrado gastos hoy y envía una notificación personalizada.
 */
class DailyReminderReceiver : BroadcastReceiver(), KoinComponent {

    private val dailyExpenseDao: DailyExpenseDao by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)

        // Verificar si ya se registraron gastos hoy
        CoroutineScope(Dispatchers.IO).launch {
            val hasExpenses = hasSpentToday()
            notificationHelper.sendDailyReminder(hasExpenses)
        }
    }

    /**
     * Verifica si el usuario ha registrado gastos hoy.
     *
     * Esta función es reutilizable y puede ser llamada desde otros componentes
     * para verificar el estado de gastos del día actual.
     *
     * @return true si hay gastos registrados hoy, false si no.
     */
    private suspend fun hasSpentToday(): Boolean {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        // TODO: Obtener userId real del usuario logueado (actualmente hardcoded a 1L)
        // En producción, esto debería venir de una sesión/SharedPreferences/DataStore
        val total = dailyExpenseDao.getTotalForDate(1L, today)
        return total?.let { it > 0 } ?: false
    }
}