package com.example.proyecto

import android.app.Application
import com.example.proyecto.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PiggyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PiggyApp)
            modules(appModules) // ⬅️ usa tu lista appModules
        }
    }
}
