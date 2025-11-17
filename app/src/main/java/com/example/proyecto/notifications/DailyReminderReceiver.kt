package com.example.proyecto.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.proyecto.data.local.AppDatabase
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)

        // Verificar si ya se registraron gastos hoy
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "piggy-db"
            ).build()

            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            // TODO: obtener userId real
            val hasExpenses = db.dailyExpenseDao().getTotalForDate(1L, today)?.let { it > 0 } ?: false

            notificationHelper.sendDailyReminder(hasExpenses)
        }
    }
}