package com.example.proyecto.presentation.flow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BudgetFormScreen(
    ui: BudgetFormViewModel.BudgetFormUi,
    onIncomeChange: (String) -> Unit,
    onRentChange: (String) -> Unit,
    onUtilitiesChange: (String) -> Unit,
    onTransportChange: (String) -> Unit,
    onOtherChange: (String) -> Unit,
    onModalityChange: (String) -> Unit = {},
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Formulario de presupuesto",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = ui.income,
                    onValueChange = onIncomeChange,
                    label = { Text("Ingreso mensual (Q)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = ui.rent,
                    onValueChange = onRentChange,
                    label = { Text("Renta (Q)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = ui.utilities,
                    onValueChange = onUtilitiesChange,
                    label = { Text("Servicios (Q)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = ui.transport,
                    onValueChange = onTransportChange,
                    label = { Text("Transporte (Q)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = ui.other,
                    onValueChange = onOtherChange,
                    label = { Text("Otros (Q)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Si tienes selector de modalidad, aquí puedes reemplazar por un Dropdown.
                OutlinedTextField(
                    value = ui.modality,
                    onValueChange = onModalityChange,
                    label = { Text("Modalidad (EXTREMO / MEDIO / ... )") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continuar")
                }
            }
        }
    }
}
