package com.example.proyecto.presentation.flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto.domain.model.BudgetData
import androidx.compose.runtime.LaunchedEffect


@Composable
fun BudgetFormRoute(
    viewModel: BudgetFormViewModel = viewModel(),
    initialModality: String,
    onSubmit: (BudgetData) -> Unit
) {
    androidx.compose.runtime.LaunchedEffect(initialModality) {
        viewModel.onModalityChange(initialModality)
    }

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
