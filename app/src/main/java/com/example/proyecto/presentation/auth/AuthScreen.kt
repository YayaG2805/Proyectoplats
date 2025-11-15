package com.example.proyecto.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(16.dp))

        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Iniciar sesión") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Registrarse") })
        }

        Spacer(Modifier.height(16.dp))

        if (ui.error != null) {
            Text(ui.error!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        if (tab == 0) {
            LoginForm(ui = ui, vm = vm)
        } else {
            RegisterForm(ui = ui, vm = vm)
        }
    }
}

@Composable
private fun LoginForm(ui: AuthUiState, vm: AuthViewModel) {
    OutlinedTextField(
        value = ui.loginEmail,
        onValueChange = vm::onLoginEmailChange,
        label = { Text("Correo electrónico") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(
        value = ui.loginPassword,
        onValueChange = vm::onLoginPasswordChange,
        label = { Text("Contraseña") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = { vm.login() },
        enabled = !ui.loading,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Text(if (ui.loading) "Entrando..." else "Entrar")
    }
}

@Composable
private fun RegisterForm(ui: AuthUiState, vm: AuthViewModel) {
    OutlinedTextField(
        value = ui.registerNombre,
        onValueChange = vm::onRegisterNombreChange,
        label = { Text("Nombre") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = ui.registerApellido,
        onValueChange = vm::onRegisterApellidoChange,
        label = { Text("Apellido") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = ui.registerEmail,
        onValueChange = vm::onRegisterEmailChange,
        label = { Text("Correo electrónico") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = ui.registerPassword,
        onValueChange = vm::onRegisterPasswordChange,
        label = { Text("Contraseña") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = ui.registerPassword2,
        onValueChange = vm::onRegisterPassword2Change,
        label = { Text("Confirmar contraseña") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = { vm.register() },
        enabled = !ui.loading,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Text(if (ui.loading) "Creando cuenta..." else "Crear cuenta")
    }
}
