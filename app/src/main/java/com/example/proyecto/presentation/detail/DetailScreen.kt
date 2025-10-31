@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.proyecto.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailScreen(
    profileId: Long,
    onBack: () -> Unit,
    vm: DetailViewModel = koinViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle #$profileId") },
                navigationIcon = { TextButton(onClick = onBack) { Text("←") } }
            )
        }
    ) { inner ->
        Column(Modifier.padding(inner).padding(16.dp)) {
            Text("Aquí verás los datos del mes guardado.", style = MaterialTheme.typography.titleMedium)
        }
    }
}
