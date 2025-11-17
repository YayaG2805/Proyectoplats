package com.example.proyecto.presentation.flow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.proyecto.domain.model.BudgetData
import org.koin.androidx.compose.koinViewModel
import com.example.proyecto.presentation.flow.BudgetFlowViewModel

/**
 * Route de Tips para Bottom Navigation.
 * Muestra los tips del presupuesto más reciente.
 */
@Composable
fun TipsBottomNavRoute(
    viewModel: TipsBottomNavViewModel = koinViewModel()
) {
    val latestBudget by viewModel.latestBudget.collectAsState()

    if (latestBudget != null) {
        TipsScreen(
            data = latestBudget!!,
            onBackToSummary = {} // No hay "volver" en bottom nav
        )
    } else {
        // Si no hay presupuesto, mostrar mensaje
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Text(
                "No hay presupuesto guardado.\nCrea uno primero en el Historial.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}