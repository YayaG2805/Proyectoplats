package com.example.proyecto.presentation.flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto.domain.model.BudgetData

@Composable
fun BudgetFormRoute(
    // Si usas Koin, podés cambiar a: viewModel: BudgetFormViewModel = org.koin.androidx.compose.koinViewModel(),
    viewModel: BudgetFormViewModel = viewModel(),
    onSubmit: (BudgetData) -> Unit
) {
    val ui = viewModel.uiState.collectAsState().value

    BudgetFormScreen(
        ui = ui,
        onIncomeChange = viewModel::onIncomeChange,
        onRentChange = viewModel::onRentChange,
        onUtilitiesChange = viewModel::onUtilitiesChange,
        onTransportChange = viewModel::onTransportChange,
        onOtherChange = viewModel::onOtherChange,
        onModalityChange = viewModel::onModalityChange,
        onSubmit = {
            val data = viewModel.buildBudgetData()
            onSubmit(data)
        }
    )
}
