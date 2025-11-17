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
    onAddNew: () -> Unit,
    onOpenDailyExpense: () -> Unit,
    onDelete: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val (showDeleteDialog, setShowDeleteDialog) = remember { mutableStateOf<Long?>(null) }

    // NUEVO: ViewModel para el estado del presupuesto
    val budgetStatusVM: BudgetStatusViewModel = koinViewModel()
    val budgetStatus by budgetStatusVM.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón para PROBAR notificación (debugging)
                SmallFloatingActionButton(
                    onClick = {
                        NotificationHelper(context).sendTestNotification()
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Icon(Icons.Default.Notifications, "Probar notificación")
                }

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
            Text("Mi historial de ahorro", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))

            // NUEVO: Widget de estado del presupuesto actual
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

            // Card de acceso rápido
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

            if (rows.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aún no tienes registros de ahorro.\n¡Crea tu primer presupuesto mensual!",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                Text(
                    "Historial de Meses Anteriores",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                HeaderRow()
                Spacer(Modifier.height(4.dp))
                rows.forEach { row ->
                    RowItem(row, onDeleteClick = { setShowDeleteDialog(row.id) })
                }
                Spacer(Modifier.weight(1f))
            }

            Button(
                onClick = onAddNew,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar nuevo registro mensual") }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { /* comparar resultados */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = rows.size >= 2
            ) {
                Text("Comparar resultados")
            }
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

            // Balance actual vs planificado
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

            // Barra de progreso
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

            // Balance restante
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

            // Alerta si está sobre presupuesto
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
            onAddNew = {},
            onOpenDailyExpense = {},
            onDelete = {}
        )
    }
}