package com.example.proyecto.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import com.example.proyecto.R
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun AuthScreen(
    onLoggedIn: () -> Unit,
    onRegistered: () -> Unit
) {
    var tab by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Image(painterResource(R.drawable.piggy_logo), contentDescription = "logo", modifier = Modifier.size(80.dp))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("PiggyMobile", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(12.dp))

        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab==0, onClick = { tab=0 }, text = { Text("Iniciar sesión") })
            Tab(selected = tab==1, onClick = { tab=1 }, text = { Text("Registrarse") })
        }
        Spacer(Modifier.height(16.dp))
        if (tab==0) LoginForm(onLoggedIn) else RegisterForm(onRegistered)
    }
}

@Composable
private fun LoginForm(onLoggedIn: ()->Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    OutlinedTextField(email, {email=it}, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(pass, {pass=it}, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(6.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row { Checkbox(false, {}); Text("Recordarme") }
        TextButton(onClick = {}) { Text("¿Olvidaste tu contraseña?") }
    }
    Spacer(Modifier.height(8.dp))
    Button(onClick = onLoggedIn, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) { Text("Entrar") }
}

@Composable
private fun RegisterForm(onRegistered: ()->Unit) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }
    var accept by remember { mutableStateOf(true) }

    OutlinedTextField(nombre, {nombre=it}, label={ Text("Nombre") }, modifier=Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(apellido, {apellido=it}, label={ Text("Apellido") }, modifier=Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(email, {email=it}, label={ Text("Correo electrónico") }, modifier=Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(pass, {pass=it}, label={ Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier=Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(pass2, {pass2=it}, label={ Text("Confirmar contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier=Modifier.fillMaxWidth())
    Spacer(Modifier.height(6.dp))
    Row { Checkbox(checked = accept, onCheckedChange = { accept = it }); Text("Acepto los Términos y condiciones") }
    Spacer(Modifier.height(8.dp))
    Button(onClick = onRegistered, enabled = accept, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) { Text("Crear cuenta") }
}

@Preview(showBackground = true)
@Composable
private fun AuthLoginPreview() {
    MaterialTheme { AuthScreen(onLoggedIn = {}, onRegistered = {}) }
}
