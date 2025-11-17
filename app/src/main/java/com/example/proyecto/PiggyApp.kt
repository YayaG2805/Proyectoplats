package com.example.proyecto

import android.app.Application
import com.example.proyecto.di.appModules
import com.example.proyecto.notifications.AlarmScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PiggyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PiggyApp)
            modules(appModules)
        }

        // Programar notificación diaria
        AlarmScheduler.scheduleDailyReminder(this)
    }
}