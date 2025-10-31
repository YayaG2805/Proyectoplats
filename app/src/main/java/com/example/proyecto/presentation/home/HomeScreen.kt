package com.example.proyecto.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNew: () -> Unit,
    onOpenDetail: (Long) -> Unit,
    vm: HomeViewModel = koinViewModel()
) {
    val ui by vm.ui.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNew) { Text("+") }
        }
    ) { inner ->
        Column(Modifier.padding(inner).padding(16.dp)) {
            Text("PiggyMobile", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(ui.welcome)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { onOpenDetail(1L) }) { Text("Ir a Detalle (demo)") }
        }
    }
}
