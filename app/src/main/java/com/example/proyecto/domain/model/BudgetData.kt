package com.example.proyecto.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BudgetData(
    val income: Double = 0.0,
    val rent: Double = 0.0,
    val utilities: Double = 0.0,
    val transport: Double = 0.0,
    val other: Double = 0.0,
    val modality: String = "MEDIO"
)
