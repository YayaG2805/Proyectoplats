package com.example.proyecto

import android.app.Application
import com.example.proyecto.di.appModules
import com.example.proyecto.notifications.AlarmScheduler
import com.example.proyecto.presentation.api.ExchangeRateViewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PiggyApp : Application() {

    // Inyectar el ViewModel para hacer la llamada a Internet
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PiggyApp)
            modules(appModules)
        }

        // Programar notificación diaria
        AlarmScheduler.scheduleDailyReminder(this)

        // ===== LLAMADA A INTERNET  =====
        // Esta llamada se ejecuta al iniciar la app, en segundo plano,
        // sin afectar la funcionalidad principal ni la UI.
        // Obtiene tipos de cambio del USD usando la API gratuita de Frankfurter.
        android.util.Log.d("PiggyApp", "🌐 Iniciando llamada a Internet para obtener tipos de cambio...")
        exchangeRateViewModel.fetchExchangeRates()
    }
}