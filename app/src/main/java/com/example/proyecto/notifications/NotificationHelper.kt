package com.example.proyecto.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.proyecto.MainActivity
import com.example.proyecto.R

/**
 * Helper para crear y enviar notificaciones en PiggyMobile.
 *
 * Maneja la creación del canal de notificaciones y el envío de recordatorios diarios.
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "daily_expense_reminder"
        const val NOTIFICATION_ID = 1001

        // Extras para navegación
        const val EXTRA_NAVIGATE_TO_DAILY_EXPENSE = "navigate_to_daily_expense"
    }

    init {
        createNotificationChannel()
    }

    /**
     * Crea el canal de notificaciones (Android 8.0+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios de gastos"
            val descriptionText = "Recordatorios diarios para registrar tus gastos"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Envía la notificación de recordatorio diario.
     *
     * El mensaje cambia según si el usuario ya registró gastos hoy o no.
     * Al tocar la notificación, abre la app directamente en la pantalla de gastos diarios.
     *
     * @param hasExpensesToday true si ya hay gastos registrados hoy
     */
    fun sendDailyReminder(hasExpensesToday: Boolean) {
        val message = if (hasExpensesToday) {
            "Ya registraste algunos gastos hoy. ¿Hay algo más que agregar?"
        } else {
            "¡Oye! ¿Ya registraste tus gastos de hoy? 🐷💰"
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Flag para que MainActivity sepa que debe navegar a DailyExpense
            putExtra(EXTRA_NAVIGATE_TO_DAILY_EXPENSE, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("PiggyMobile - Recordatorio")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
        }
    }

    /**
     * Envía una notificación de prueba inmediatamente.
     *
     * Útil para debugging y verificar que el sistema de notificaciones funciona.
     * Simula tener gastos registrados hoy.
     */
    fun sendTestNotification() {
        sendDailyReminder(hasExpensesToday = true)
    }
}