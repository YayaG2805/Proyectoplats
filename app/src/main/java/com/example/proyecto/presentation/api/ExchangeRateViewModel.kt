package com.example.proyecto.presentation.api

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para demostrar llamada a Internet.
 *
 * IMPORTANTE: Este ViewModel es SOLO para cumplir con el requisito
 * de la entrega final. Se ejecuta en segundo plano sin afectar la UI.
 *
 * La llamada se hace al inicializar la app y obtiene tipos de cambio
 * del USD para referencia (aunque no se muestran en la UI por ahora).
 */
class ExchangeRateViewModel : ViewModel() {

    private val TAG = "ExchangeRateVM"

    private val _exchangeRateUSD = MutableStateFlow<Double?>(null)
    val exchangeRateUSD: StateFlow<Double?> = _exchangeRateUSD.asStateFlow()

    private val _lastUpdate = MutableStateFlow<String?>(null)
    val lastUpdate: StateFlow<String?> = _lastUpdate.asStateFlow()

    /**
     * Obtiene los tipos de cambio del USD.
     * Esta función se llama automáticamente al iniciar la app.
     */
    fun fetchExchangeRates() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Iniciando llamada a API de tipos de cambio...")

                val response = RetrofitClient.exchangeRateService.getExchangeRates()

                if (response.isSuccessful) {
                    val data = response.body()

                    if (data != null) {
                        // Obtener tipo de cambio USD -> GTQ (Quetzal Guatemalteco)
                        // Si no existe GTQ, guardar EUR como ejemplo
                        val gtqRate = data.rates["GTQ"] ?: data.rates["EUR"]

                        _exchangeRateUSD.value = gtqRate
                        _lastUpdate.value = data.date

                        Log.d(TAG, "✅ Llamada exitosa - Tipo de cambio USD: $gtqRate")
                        Log.d(TAG, "Fecha: ${data.date}")
                        Log.d(TAG, "Monedas disponibles: ${data.rates.keys}")
                    } else {
                        Log.w(TAG, "⚠️ Respuesta vacía de la API")
                    }
                } else {
                    Log.e(TAG, "❌ Error en la respuesta: ${response.code()} - ${response.message()}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error en llamada a API: ${e.message}", e)
            }
        }
    }

    init {
        // Llamada automática al crear el ViewModel
        fetchExchangeRates()
        Log.d(TAG, "📡 ExchangeRateViewModel inicializado - Llamada a Internet en progreso")
    }
}