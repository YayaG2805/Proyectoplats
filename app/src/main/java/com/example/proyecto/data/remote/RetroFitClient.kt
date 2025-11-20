package com.example.proyecto.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit para llamadas a APIs externas.
 *
 * IMPORTANTE: Este cliente es SOLO para cumplir con el requisito
 * de llamada a Internet de la entrega final. No afecta la
 * funcionalidad principal de PiggyMobile.
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.frankfurter.app/"

    /**
     * Cliente OkHttp con logging para debugging.
     */
    private val okHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Instancia de Retrofit.
     */
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Servicio de tipos de cambio.
     */
    val exchangeRateService: ExchangeRateService by lazy {
        retrofit.create(ExchangeRateService::class.java)
    }
}