package com.example.proyecto.presentation.budgetstatus

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale
import org.koin.androidx.compose.koinViewModel

/**
 * Pantalla de estado del presupuesto que sincroniza gastos diarios con el plan mensual.
 *
 * Muestra:
 * - Presupuesto mensual planificado
 * - Gastos diarios acumulados del mes
 * - Balance actual vs planificado
 * - Alertas si se está excediendo
 * - Sugerencias de gasto diario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetStatusScreen(
    onAddDailyExpense: () -> Unit = {},
    onViewBudgetDetails: () -> Unit = {},
    vm: BudgetStatusViewModel = koinViewModel()
) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estado del Presupuesto") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (!uiState.hasMonthlyBudget) {
            // No hay presupuesto mensual
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "No tienes un presupuesto mensual registrado",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Crea tu primer presupuesto para empezar a controlar tus gastos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = onViewBudgetDetails) {
                        Text("Crear presupuesto")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header del mes
                Text(
                    uiState.currentMonth,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Alerta si está sobre presupuesto
                if (uiState.isOverBudget) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Column {
                                Text(
                                    "¡Alerta! Has excedido tu presupuesto",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    "Estás gastando más de lo planificado. Considera reducir gastos innecesarios.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                // Tarjeta de resumen principal
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (uiState.isOverBudget)
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Balance Actual",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            uiState.actualBalance.q(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isOverBudget)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                        )

                        LinearProgressIndicator(
                            progress = { ((uiState.percentageSpent / 100).coerceIn(0.0, 1.0)).toFloat() },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (uiState.percentageSpent > 100)
                                MaterialTheme.colorScheme.error
                            else if (uiState.percentageSpent > 75)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.primary,
                        )

                        Text(
                            "${uiState.percentageSpent.toInt()}% del presupuesto variable utilizado",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Detalles del presupuesto mensual
                Text(
                    "Presupuesto Mensual",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BudgetRow("Ingreso", uiState.monthlyIncome.q(), Icons.Default.AccountBalance)
                        BudgetRow("Gastos fijos", uiState.monthlyFixedExpenses.q(), Icons.Default.Home)
                        Divider()
                        BudgetRow(
                            "Disponible para gastos variables",
                            uiState.monthlyPlannedBalance.q(),
                            Icons.Default.Wallet,
                            highlighted = true
                        )
                        Text(
                            "Modalidad: ${uiState.modality}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Gastos diarios del mes
                Text(
                    "Gastos Variables Este Mes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BudgetRow(
                            "Total gastado",
                            uiState.dailyExpensesThisMonth.q(),
                            Icons.Default.ShoppingCart,
                            highlighted = true
                        )
                        BudgetRow(
                            "Número de registros",
                            "${uiState.dailyExpensesCount} gastos",
                            Icons.Default.List
                        )
                        BudgetRow(
                            "Promedio diario",
                            uiState.averageDailySpending.q(),
                            Icons.Default.Timeline
                        )
                    }
                }

                // Proyección y sugerencias
                Text(
                    "Proyección del Mes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BudgetRow(
                            "Días restantes",
                            "${uiState.daysLeftInMonth} días",
                            Icons.Default.CalendarToday
                        )

                        if (uiState.suggestedDailyLimit > 0) {
                            Divider()
                            BudgetRow(
                                "Límite diario sugerido",
                                uiState.suggestedDailyLimit.q(),
                                Icons.Default.Lightbulb,
                                highlighted = true
                            )
                            Text(
                                "Para cumplir tu meta de ahorro, intenta no gastar más de ${uiState.suggestedDailyLimit.q()} diarios el resto del mes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalButton(
                        onClick = onAddDailyExpense,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Registrar gasto")
                    }

                    OutlinedButton(
                        onClick = onViewBudgetDetails,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Info, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ver detalles")
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun BudgetRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (highlighted)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (highlighted)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            value,
            style = if (highlighted)
                MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            else
                MaterialTheme.typography.bodyMedium,
            color = if (highlighted)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun Double.q(): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "GT"))
    return nf.format(this)
}