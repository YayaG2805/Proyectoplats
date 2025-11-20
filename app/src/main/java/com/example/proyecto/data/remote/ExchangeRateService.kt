package com.example.proyecto.data.remote

import retrofit2.Response
import retrofit2.http.GET

/**
 * Servicio de API para obtener tipos de cambio.
 *
 * Usa la API gratuita de Frankfurter (sin necesidad de API key).
 * Este servicio es SOLO para cumplir con el requisito de llamada a Internet.
 * No afecta la funcionalidad principal de la app.
 */
interface ExchangeRateService {

    /**
     * Obtiene los tipos de cambio actuales del USD.
     * Endpoint: https://api.frankfurter.app/latest?from=USD
     */
    @GET("latest?from=USD")
    suspend fun getExchangeRates(): Response<ExchangeRateResponse>
}

/**
 * Respuesta de la API de tipos de cambio.
 */
data class ExchangeRateResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)