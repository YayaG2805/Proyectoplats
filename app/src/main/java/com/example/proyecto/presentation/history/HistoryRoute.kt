package com.example.proyecto.presentation.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.proyecto.presentation.navigation.DailyExpenseDest
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryRoute(
    onEditCurrent: () -> Unit,
    onViewSavingsIndex: () -> Unit,
    navController: NavController? = null
) {
    val vm: HistoryViewModel = koinViewModel()
    val rows by vm.rows.collectAsState()
    val currentMonthBudget by vm.currentMonthBudget.collectAsState()

    var showAddIncomeDialog by remember { mutableStateOf(false) }

    HistoryScreen(
        rows = rows,
        currentMonthBudget = currentMonthBudget,
        onEditCurrent = onEditCurrent,
        onAddExtraIncome = {
            showAddIncomeDialog = true
        },
        onViewSavingsIndex = onViewSavingsIndex,
        onOpenDailyExpense = {
            navController?.navigate(DailyExpenseDest)
        },
        onDelete = { budgetId ->
            vm.deleteById(budgetId)
        }
    )

    // Diálogo para agregar ingreso extra
    if (showAddIncomeDialog) {
        AddIncomeDialog(
            onDismiss = { showAddIncomeDialog = false },
            onConfirm = { amount ->
                vm.addExtraIncome(amount)
                showAddIncomeDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddIncomeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Ingreso Extra") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "¿Recibiste un ingreso adicional este mes? (ej: bono, freelance, regalo)",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto (Q)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > 0) {
                        onConfirm(amountValue)
                    }
                },
                enabled = amount.toDoubleOrNull()?.let { it > 0 } == true
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}