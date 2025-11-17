package com.example.proyecto.presentation.dailyexpense

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto.domain.model.DailyExpense
import com.example.proyecto.domain.model.ExpenseCategory
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyExpenseScreen(
    expenses: List<DailyExpense>,
    totalToday: Double,
    suggestedDailyLimit: Double = 0.0, // Nuevo parámetro
    onAddExpense: (category: ExpenseCategory, amount: Double, description: String) -> Unit,
    onDeleteExpense: (Long) -> Unit,
    onBack: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val today = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) }

    // Calcular si está cerca o sobre el límite
    val isNearLimit = suggestedDailyLimit > 0 && totalToday >= suggestedDailyLimit * 0.80
    val isOverLimit = suggestedDailyLimit > 0 && totalToday > suggestedDailyLimit

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gastos de hoy") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("←") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, "Agregar gasto")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Resumen del día con alertas
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isOverLimit -> MaterialTheme.colorScheme.errorContainer
                        isNearLimit -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Total gastado hoy ($today)",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                totalToday.q(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isOverLimit -> MaterialTheme.colorScheme.error
                                    isNearLimit -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                                }
                            )
                        }

                        if (suggestedDailyLimit > 0) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Límite sugerido",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    suggestedDailyLimit.q(),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    if (suggestedDailyLimit > 0) {
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { ((totalToday / suggestedDailyLimit).coerceIn(0.0, 1.0)).toFloat() },
                            modifier = Modifier.fillMaxWidth(),
                            color = when {
                                isOverLimit -> MaterialTheme.colorScheme.error
                                isNearLimit -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.primary
                            },
                        )

                        Text(
                            "${((totalToday / suggestedDailyLimit) * 100).toInt()}% del límite diario",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Text(
                        "${expenses.size} ${if (expenses.size == 1) "registro" else "registros"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Alerta si está sobre el límite
            if (isOverLimit) {
                Spacer(Modifier.height(12.dp))
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
                        Column {
                            Text(
                                "¡Has excedido tu límite diario!",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                "Intenta reducir gastos el resto del día para cumplir tu meta mensual.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            } else if (isNearLimit) {
                Spacer(Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Atención: Estás cerca de tu límite diario. Te quedan ${(suggestedDailyLimit - totalToday).q()}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aún no has registrado gastos hoy.\n¡Presiona + para agregar!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    "Detalle de gastos:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        ExpenseItem(
                            expense = expense,
                            onDelete = { onDeleteExpense(expense.id) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddExpenseDialog(
            suggestedDailyLimit = suggestedDailyLimit,
            currentTotal = totalToday,
            onDismiss = { showDialog = false },
            onConfirm = { category, amount, description ->
                onAddExpense(category, amount, description)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ExpenseItem(
    expense: DailyExpense,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    expense.category.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                if (expense.description.isNotBlank()) {
                    Text(
                        expense.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    expense.amount.q(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseDialog(
    suggestedDailyLimit: Double,
    currentTotal: Double,
    onDismiss: () -> Unit,
    onConfirm: (ExpenseCategory, Double, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.COMIDA) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showWarning by remember { mutableStateOf(false) }

    // Verificar si el nuevo gasto excedería el límite
    val amountValue = amount.toDoubleOrNull() ?: 0.0
    val wouldExceedLimit = suggestedDailyLimit > 0 && (currentTotal + amountValue) > suggestedDailyLimit

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar gasto") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Selector de categoría
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ExpenseCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Monto
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        showWarning = false
                    },
                    label = { Text("Monto (Q)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = wouldExceedLimit
                )

                // ALERTA de límite excedido
                if (wouldExceedLimit) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
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
                            Column {
                                Text(
                                    "⚠️ Excederás tu límite diario",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    "Con este gasto tendrás ${(currentTotal + amountValue).q()} de ${suggestedDailyLimit.q()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    "¿Realmente necesitas esto hoy?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }

                // Descripción
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
                        // Si excede el límite, mostrar confirmación adicional
                        if (wouldExceedLimit && !showWarning) {
                            showWarning = true
                        } else {
                            onConfirm(selectedCategory, amountValue, description)
                        }
                    }
                },
                enabled = amount.toDoubleOrNull()?.let { it > 0 } == true,
                colors = if (wouldExceedLimit && showWarning) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                } else ButtonDefaults.buttonColors()
            ) {
                Text(when {
                    wouldExceedLimit && !showWarning -> "Confirmar de todas formas"
                    wouldExceedLimit && showWarning -> "Sí, registrar gasto"
                    else -> "Guardar"
                })
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun Double.q(): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "GT"))
    return nf.format(this)
}