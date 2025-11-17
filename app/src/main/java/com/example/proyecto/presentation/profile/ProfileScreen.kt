package com.example.proyecto.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

/**
 * Pantalla de perfil del usuario con datos completos y sincronizados.
 * CORREGIDO: Muestra gastos fijos y gastos diarios por separado.
 */
@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    vm: ProfileViewModel = koinViewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordChangeSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con avatar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = uiState.userName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = uiState.userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        // Estadísticas SINCRONIZADAS
        Text(
            "Estadísticas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        // Ahorro total destacado
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Savings,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Ahorro Planificado",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    "Q${uiState.totalSavingsPlanned}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (uiState.savingPercentage > 0) {
                    Text(
                        "${uiState.savingPercentage.roundToInt()}% de tus ingresos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // Primera fila: Meses y Registros Diarios
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Meses",
                value = uiState.totalMonths.toString(),
                icon = Icons.Default.DateRange
            )

            StatCard(
                modifier = Modifier.weight(1f),
                title = "Registros",
                value = uiState.totalDailyExpenses.toString(),
                icon = Icons.Default.Receipt
            )
        }

        // Segunda fila: Ingreso Total y Gastos Fijos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Ingreso Total",
                value = "Q${uiState.totalIncome}",
                icon = Icons.Default.AccountBalance
            )

            StatCard(
                modifier = Modifier.weight(1f),
                title = "Gastos Fijos",
                value = "Q${uiState.totalFixedExpenses}",
                icon = Icons.Default.Home
            )
        }

        // Tercera fila: Gastos Diarios (NUEVO - separado de gastos fijos)
        StatCard(
            modifier = Modifier.fillMaxWidth(),
            title = "Gastos Variables",
            subtitle = "Total gastado en tus registros diarios",
            value = "Q${uiState.totalDailyExpensesAmount}",
            icon = Icons.Default.ShoppingCart,
            highlighted = true
        )

        // Configuración
        Text(
            "Configuración",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        SettingItem(
            icon = Icons.Default.Notifications,
            title = "Notificaciones diarias",
            subtitle = "Recordatorio a las 8:00 PM",
            checked = uiState.notificationsEnabled,
            onCheckedChange = vm::toggleNotifications
        )

        SettingItem(
            icon = Icons.Default.Warning,
            title = "Alertas de presupuesto",
            subtitle = "Aviso cuando excedes el límite",
            checked = uiState.budgetAlertsEnabled,
            onCheckedChange = vm::toggleBudgetAlerts
        )

        Divider()

        // Acciones
        Text(
            "Cuenta",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showPasswordDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Lock, "Contraseña")
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Cambiar contraseña", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Última modificación: ${uiState.lastPasswordChange}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.Default.KeyboardArrowRight, null)
            }
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showLogoutDialog = true },
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    "Cerrar sesión",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    "Cerrar sesión",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Info de versión
        Spacer(Modifier.height(8.dp))

        Text(
            "PiggyMobile v${uiState.appVersion}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    // Snackbar de éxito al cambiar contraseña
    if (passwordChangeSuccess) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            passwordChangeSuccess = false
        }
    }

    // Diálogo de cambio de contraseña
    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = {
                showPasswordDialog = false
            },
            onConfirm = { current, new ->
                vm.changePassword(
                    currentPassword = current,
                    newPassword = new,
                    onSuccess = {
                        showPasswordDialog = false
                        passwordChangeSuccess = true
                    },
                    onError = { error ->
                        // El error se muestra en el diálogo
                    }
                )
            }
        )
    }

    // Diálogo de confirmación de cierre de sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Default.ExitToApp, null) },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        vm.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Lock, null) },
        title = { Text("Cambiar Contraseña") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            errorMessage!!,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        errorMessage = null
                    },
                    label = { Text("Contraseña actual") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = null
                    },
                    label = { Text("Nueva contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    label = { Text("Confirmar nueva contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    "La contraseña debe tener al menos 6 caracteres",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                            errorMessage = "Todos los campos son obligatorios"
                        }
                        newPassword.length < 6 -> {
                            errorMessage = "La nueva contraseña debe tener al menos 6 caracteres"
                        }
                        newPassword != confirmPassword -> {
                            errorMessage = "Las contraseñas no coinciden"
                        }
                        currentPassword == newPassword -> {
                            errorMessage = "La nueva contraseña debe ser diferente a la actual"
                        }
                        else -> {
                            onConfirm(currentPassword, newPassword)
                        }
                    }
                }
            ) {
                Text("Cambiar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlighted: Boolean = false
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (highlighted)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlighted)
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}