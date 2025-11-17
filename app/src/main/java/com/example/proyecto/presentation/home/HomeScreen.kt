package com.example.proyecto.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto.R
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo de PiggyMobile
            Image(
                painter = painterResource(id = R.drawable.piggy_logo),
                contentDescription = "PiggyMobile logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "PiggyMobile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                ui.welcome,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(24.dp))

            Button(onClick = { onOpenDetail(1L) }) {
                Text("Ir a Detalle (demo)")
            }
        }
    }
}