@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.proyecto.presentation.newprofile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.proyecto.domain.model.BudgetData

/**
 * Pantalla para crear un nuevo mes de presupuesto.
 *
 * Permite al usuario:
 * - Ingresar datos de presupuesto completos
 * - Cambiar modalidad de ahorro con dropdown
 * - Guardar en el historial
 */
@Composable
fun NewProfileScreen(
    onSaved: (Long) -> Unit,
    onBack: () -> Unit,
    vm: NewProfileViewModel = koinViewModel()
) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Mes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Completa los datos de tu presupuesto mensual",
                style = MaterialTheme.typography.titleMedium
            )

            // Selector de modalidad con dropdown
            var expandedModality by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedModality,
                onExpandedChange = { expandedModality = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.modality,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Modalidad de ahorro") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedModality) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedModality,
                    onDismissRequest = { expandedModality = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("EXTREMO", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Maximiza tu ahorro reduciendo gastos al mínimo",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        onClick = {
                            vm.setModality("EXTREMO")
                            expandedModality = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("MEDIO", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Un balance entre ahorro y gastos personales",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        onClick = {
                            vm.setModality("MEDIO")
                            expandedModality = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("IMPREVISTO", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Reserva un fondo para imprevistos",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        onClick = {
                            vm.setModality("IMPREVISTO")
                            expandedModality = false
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Formulario
            OutlinedTextField(
                value = uiState.income,
                onValueChange = vm::onIncomeChange,
                label = { Text("Ingreso mensual (Q)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.incomeError != null,
                supportingText = uiState.incomeError?.let { { Text(it) } }
            )

            OutlinedTextField(
                value = uiState.rent,
                onValueChange = vm::onRentChange,
                label = { Text("Renta (Q)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.utilities,
                onValueChange = vm::onUtilitiesChange,
                label = { Text("Servicios (Q)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.transport,
                onValueChange = vm::onTransportChange,
                label = { Text("Transporte (Q)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.other,
                onValueChange = vm::onOtherChange,
                label = { Text("Otros gastos (Q)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.weight(1f))

            // Mostrar error general si existe
            if (uiState.error != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        uiState.error!!,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Botón guardar
            Button(
                onClick = { vm.save(onSaved) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (uiState.isSaving) "Guardando..." else "Guardar presupuesto")
            }
        }
    }
}