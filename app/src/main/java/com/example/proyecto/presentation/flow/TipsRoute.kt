package com.example.proyecto.presentation.flow

import androidx.compose.runtime.Composable
import com.example.proyecto.domain.model.BudgetData

@Composable
fun TipsRoute(data: BudgetData, onBack: ()->Unit) {
    TipsScreen(data = data, onBackToSummary = onBack)
}