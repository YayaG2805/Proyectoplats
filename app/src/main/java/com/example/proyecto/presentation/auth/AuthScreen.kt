package com.example.proyecto.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyecto.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    onLoggedIn: () -> Unit,
    onRegistered: () -> Unit,
    vm: AuthViewModel = koinViewModel()
) {
    val ui by vm.ui.collectAsState()

    var tab by remember { mutableStateOf(0) }

    // Navegación al detectar éxito
    LaunchedEffect(ui.loginSuccess, ui.registerSuccess) {
        if (ui.loginSuccess) {
            onLoggedIn()
            vm.clearNavigationFlags()
        }
        if (ui.registerSuccess) {
            onRegistered()
            vm.clearNavigationFlags()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // Logo centrado en la parte superior
        Image(
            painter = painterResource(id = R.drawable.piggy_logo),
            contentDescription = "PiggyMobile logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "PiggyMobile",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            "Tu aliado de ahorro personal",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        TabRow(selectedTabIndex = tab) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text("Iniciar sesión") }
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text("Registrarse") }
            )
        }

        Spacer(Modifier.height(24.dp))

        if (ui.error != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    ui.error!!,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        if (tab == 0) {
            LoginForm(ui = ui, vm = vm)
        } else {
            RegisterForm(ui = ui, vm = vm)
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun LoginForm(ui: AuthUiState, vm: AuthViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = ui.loginEmail,
            onValueChange = vm::onLoginEmailChange,
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !ui.loading
        )

        OutlinedTextField(
            value = ui.loginPassword,
            onValueChange = vm::onLoginPasswordChange,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !ui.loading
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { vm.login() },
            enabled = !ui.loading,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            if (ui.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(if (ui.loading) "Entrando..." else "Entrar")
        }
    }
}

@Composable
private fun RegisterForm(ui: AuthUiState, vm: AuthViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = ui.registerNombre,
            onValueChange = vm::onRegisterNombreChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !ui.loading
        )

        OutlinedTextField(
            value = ui.registerApellido,
            onValueChange = vm::onRegisterApellidoChange,
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !ui.loading
        )

        OutlinedTextField(
            value = ui.registerEmail,
            onValueChange = vm::onRegisterEmailChange,
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !ui.loading
        )

        OutlinedTextField(
            value = ui.registerPassword,
            onValueChange = vm::onRegisterPasswordChange,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !ui.loading
        )

        OutlinedTextField(
            value = ui.registerPassword2,
            onValueChange = vm::onRegisterPassword2Change,
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !ui.loading
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { vm.register() },
            enabled = !ui.loading,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            if (ui.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(if (ui.loading) "Creando cuenta..." else "Crear cuenta")
        }
    }
}