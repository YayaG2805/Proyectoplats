package com.example.proyecto.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyecto.notifications.NotificationHelper
import com.example.proyecto.presentation.budgetstatus.BudgetStatusViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

data class HistoryRow(
    val id: Long = 0,
    val mes: String,
    val ingreso: String,
    val ahorro: String,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    rows: List<HistoryRow>,
    currentMonthBudget: com.example.proyecto.data.local.MonthlyBudgetEntity?,
    onEditCurrent: () -> Unit,
    onAddExtraIncome: () -> Unit,
    onViewSavingsIndex: () -> Unit,
    onOpenDailyExpense: () -> Unit,
    onDelete: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val (showDeleteDialog, setShowDeleteDialog) = remember { mutableStateOf<Long?>(null) }

    // ViewModel para el estado del presupuesto
    val budgetStatusVM: BudgetStatusViewModel = koinViewModel()
    val budgetStatus by budgetStatusVM.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón para gastos diarios
                FloatingActionButton(
                    onClick = onOpenDailyExpense,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Add, "Registrar gasto de hoy")
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Mi presupuesto mensual", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))

            // Widget de estado del presupuesto actual
            if (budgetStatus.hasMonthlyBudget) {
                BudgetStatusWidget(
                    monthName = budgetStatus.currentMonth,
                    plannedBalance = budgetStatus.monthlyPlannedBalance,
                    dailyExpenses = budgetStatus.dailyExpensesThisMonth,
                    actualBalance = budgetStatus.actualBalance,
                    isOverBudget = budgetStatus.isOverBudget,
                    percentageSpent = budgetStatus.percentageSpent,
                    suggestedDailyLimit = budgetStatus.suggestedDailyLimit,
                    onAddExpense = onOpenDailyExpense
                )
                Spacer(Modifier.height(16.dp))
            }

            // Card de acceso rápido a gastos diarios
            ElevatedCard(
                onClick = onOpenDailyExpense,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Registro diario de gastos",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Registra lo que gastas hoy 📝",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Sección del mes actual
            Text(
                "Presupuesto del Mes Actual",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))

            if (currentMonthBudget == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No tienes presupuesto para este mes",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Crea tu presupuesto mensual para empezar",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onEditCurrent) {
                            Text("Crear presupuesto")
                        }
                    }
                }
            } else {
                CurrentMonthCard(
                    budget = currentMonthBudget,
                    onEdit = onEditCurrent,
                    onAddIncome = onAddExtraIncome
                )
            }

            Spacer(Modifier.height(16.dp))

            // Botón de Índice de Ahorro (reemplaza "Comparar resultados")
            Button(
                onClick = onViewSavingsIndex,
                modifier = Modifier.fillMaxWidth(),
                enabled = rows.isNotEmpty()
            ) {
                Icon(Icons.Default.ShowChart, null)
                Spacer(Modifier.width(8.dp))
                Text("Ver mi índice de ahorro")
            }

            Spacer(Modifier.height(16.dp))

            // Historial de meses anteriores
            if (rows.filter { it.id != currentMonthBudget?.id }.isNotEmpty()) {
                Text(
                    "Historial de Meses Anteriores",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                HeaderRow()
                Spacer(Modifier.height(4.dp))

                rows.filter { it.id != currentMonthBudget?.id }.forEach { row ->
                    RowItem(row, onDeleteClick = { setShowDeleteDialog(row.id) })
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { setShowDeleteDialog(null) },
            icon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("¿Eliminar registro?") },
            text = { Text("Esta acción no se puede deshacer. ¿Estás seguro?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(showDeleteDialog)
                        setShowDeleteDialog(null)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowDeleteDialog(null) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CurrentMonthCard(
    budget: com.example.proyecto.data.local.MonthlyBudgetEntity,
    onEdit: () -> Unit,
    onAddIncome: () -> Unit
) {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "GT"))
    val totalExpenses = budget.rent + budget.utilities + budget.transport + budget.other
    val savings = budget.income - totalExpenses

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Ingreso",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        nf.format(budget.income),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Chip(
                    label = { Text(budget.modality) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Gastos fijos", style = MaterialTheme.typography.bodyMedium)
                Text(
                    nf.format(totalExpenses),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ahorro planificado", style = MaterialTheme.typography.bodyMedium)
                Text(
                    nf.format(savings),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (savings > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Editar")
                }

                FilledTonalButton(
                    onClick = onAddIncome,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("+ Ingreso")
                }
            }
        }
    }
}

@Composable
private fun Chip(label: @Composable () -> Unit, colors: ChipColors) {
    AssistChip(
        onClick = {},
        label = label,
        colors = colors,
        enabled = false
    )
}

/**
 * Widget que muestra el estado actual del presupuesto mensual vs gastos diarios.
 */
@Composable
private fun BudgetStatusWidget(
    monthName: String,
    plannedBalance: Double,
    dailyExpenses: Double,
    actualBalance: Double,
    isOverBudget: Boolean,
    percentageSpent: Double,
    suggestedDailyLimit: Double,
    onAddExpense: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isOverBudget)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Estado del Mes Actual",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    monthName,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Disponible planificado",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        plannedBalance.q(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        "Gastado hasta hoy",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        dailyExpenses.q(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isOverBudget)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { ((percentageSpent / 100).coerceIn(0.0, 1.0)).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = if (percentageSpent > 100)
                        MaterialTheme.colorScheme.error
                    else if (percentageSpent > 75)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.primary,
                )
                Text(
                    "${percentageSpent.toInt()}% utilizado",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Balance Restante",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        actualBalance.q(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverBudget)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }

                if (suggestedDailyLimit > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Límite diario sugerido",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            suggestedDailyLimit.q(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            if (isOverBudget) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Has excedido tu presupuesto variable. Intenta reducir gastos innecesarios.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderRow() {
    Row(Modifier.fillMaxWidth()) {
        Text("Mes", Modifier.weight(1.2f), style = MaterialTheme.typography.labelLarge)
        Text("Ingreso", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Text("Ahorro", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Text("Estado", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(40.dp))
    }
}

@Composable
private fun RowItem(r: HistoryRow, onDeleteClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(r.mes, Modifier.weight(1.2f), style = MaterialTheme.typography.bodySmall)
        Text(r.ingreso, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text(r.ahorro, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)

        val estadoColor = when (r.estado) {
            "Cumplido" -> MaterialTheme.colorScheme.primary
            "Parcial" -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.error
        }
        Text(
            r.estado,
            Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = estadoColor
        )

        IconButton(onClick = onDeleteClick, modifier = Modifier.size(40.dp)) {
            Icon(
                Icons.Default.Delete,
                "Eliminar",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun Double.q(): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "GT"))
    return nf.format(this)
}

@Preview(showBackground = true)
@Composable
private fun HistoryPreview() {
    MaterialTheme {
        HistoryScreen(
            rows = listOf(
                HistoryRow(1, "Marzo 2025", "Q 7000", "Q 700", "Cumplido"),
                HistoryRow(2, "Febrero 2025", "Q 7000", "Q 500", "Parcial"),
                HistoryRow(3, "Enero 2025", "Q 7000", "Q 300", "No cumplido"),
            ),
            currentMonthBudget = null,
            onEditCurrent = {},
            onAddExtraIncome = {},
            onViewSavingsIndex = {},
            onOpenDailyExpense = {},
            onDelete = {}
        )
    }
}