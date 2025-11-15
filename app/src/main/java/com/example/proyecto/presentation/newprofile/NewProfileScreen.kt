@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.proyecto.presentation.newprofile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewProfileScreen(
    onSaved: (Long) -> Unit,
    onBack: () -> Unit,
    onChangeModality: () -> Unit,
    vm: NewProfileViewModel = koinViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Mes") },
                navigationIcon = { TextButton(onClick = onBack) { Text("←") } }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(16.dp)
        ) {
            Text(
                "Formulario (placeholder)",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onChangeModality,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Nueva modalidad")
            }

            Spacer(Modifier.height(12.dp))

            Button(onClick = { onSaved(999L) }) {
                Text("Guardar (demo)")
            }
        }
    }
}