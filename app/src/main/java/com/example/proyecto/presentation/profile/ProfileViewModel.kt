package com.example.proyecto.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.DailyExpenseDao
import com.example.proyecto.data.local.MonthlyBudgetDao
import com.example.proyecto.data.local.UserDao
import com.example.proyecto.data.local.UserPreferences
import com.example.proyecto.domain.model.UserSession
import com.example.proyecto.presentation.history.HistoryViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ProfileUiState(
    val userName: String = "Usuario",
    val userEmail: String = "usuario@piggymobile.com",
    val totalMonths: Int = 0,
    val totalDailyExpenses: Int = 0, // Número de registros diarios
    val totalSavingsPlanned: String = "0", // Ahorro planificado (ingreso - gastos fijos)
    val totalIncome: String = "0",
    val totalFixedExpenses: String = "0", // Solo gastos fijos del presupuesto
    val totalDailyExpensesAmount: String = "0", // NUEVO: Total gastado en gastos diarios
    val savingPercentage: Double = 0.0,
    val notificationsEnabled: Boolean = true,
    val budgetAlertsEnabled: Boolean = true,
    val lastPasswordChange: String = "Nunca",
    val appVersion: String = "1.0.0"
)

class ProfileViewModel(
    private val dailyExpenseDao: DailyExpenseDao,
    private val monthlyBudgetDao: MonthlyBudgetDao,
    private val userDao: UserDao,
    private val historyViewModel: HistoryViewModel,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadStatistics()
    }

    /**
     * Carga los datos del usuario desde DataStore.
     */
    private fun loadUserData() {
        viewModelScope.launch {
            // Combinar flows del DataStore
            combine(
                userPreferences.userName,
                userPreferences.userLastName,
                userPreferences.userEmail
            ) { nombre, apellido, email ->
                Triple(nombre, apellido, email)
            }.collect { (nombre, apellido, email) ->
                val fullName = if (nombre.isNotBlank() && apellido.isNotBlank()) {
                    "$nombre $apellido"
                } else {
                    "Usuario PiggyMobile"
                }

                _uiState.update {
                    it.copy(
                        userName = fullName,
                        userEmail = email.ifBlank { "usuario@piggymobile.com" }
                    )
                }
            }
        }
    }

    /**
     * Carga estadísticas del usuario desde el historial y gastos.
     * CORREGIDO: Ahora incluye gastos diarios en el total.
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            userPreferences.userId.collect { userId ->
                if (userId != null) {
                    // Combinar presupuestos mensuales Y gastos diarios
                    combine(
                        monthlyBudgetDao.getAllByUser(userId),
                        dailyExpenseDao.getRecentExpenses(userId)
                    ) { budgets, dailyExpenses ->
                        val totalMonths = budgets.size

                        // Calcular totales de presupuestos mensuales
                        val totalIncome = budgets.sumOf { it.income }
                        val totalFixedExpenses = budgets.sumOf {
                            it.rent + it.utilities + it.transport + it.other
                        }

                        // NUEVO: Sumar TODOS los gastos diarios
                        val totalDailyExpensesAmount = dailyExpenses.sumOf { it.amount }

                        // Ahorro planificado (sin contar gastos diarios)
                        val totalPlannedSavings = totalIncome - totalFixedExpenses

                        // Calcular ahorro porcentual
                        val savingPercentage = if (totalIncome > 0) {
                            (totalPlannedSavings / totalIncome) * 100
                        } else 0.0

                        ProfileUiState(
                            userName = _uiState.value.userName,
                            userEmail = _uiState.value.userEmail,
                            totalMonths = totalMonths,
                            totalDailyExpenses = dailyExpenses.size,
                            totalSavingsPlanned = String.format("%.0f", totalPlannedSavings),
                            totalIncome = String.format("%.0f", totalIncome),
                            totalFixedExpenses = String.format("%.0f", totalFixedExpenses),
                            totalDailyExpensesAmount = String.format("%.0f", totalDailyExpensesAmount),
                            savingPercentage = savingPercentage,
                            notificationsEnabled = _uiState.value.notificationsEnabled,
                            budgetAlertsEnabled = _uiState.value.budgetAlertsEnabled,
                            lastPasswordChange = _uiState.value.lastPasswordChange,
                            appVersion = _uiState.value.appVersion
                        )
                    }.collect { newState ->
                        _uiState.value = newState
                    }
                }
            }
        }
    }

    /**
     * Activa/desactiva las notificaciones diarias.
     */
    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }

        viewModelScope.launch {
            // TODO: Guardar en DataStore para persistir la preferencia
            // Si enabled: programar notificaciones
            // Si !enabled: cancelar notificaciones
        }
    }

    /**
     * Activa/desactiva las alertas de presupuesto.
     */
    fun toggleBudgetAlerts(enabled: Boolean) {
        _uiState.update { it.copy(budgetAlertsEnabled = enabled) }

        viewModelScope.launch {
            // TODO: Guardar preferencia en DataStore
        }
    }

    /**
     * Cambia la contraseña del usuario.
     * CORREGIDO: Ya no limpia los datos después de cambiar contraseña.
     */
    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userId = userPreferences.userId.first()
                if (userId == null) {
                    onError("No hay sesión activa")
                    return@launch
                }

                val user = userDao.getById(userId)
                if (user == null) {
                    onError("Usuario no encontrado")
                    return@launch
                }

                // Verificar contraseña actual
                if (user.password != currentPassword) {
                    onError("Contraseña actual incorrecta")
                    return@launch
                }

                // Validar nueva contraseña
                if (newPassword.length < 6) {
                    onError("La nueva contraseña debe tener al menos 6 caracteres")
                    return@launch
                }

                // Actualizar contraseña
                val updatedUser = user.copy(password = newPassword)
                userDao.update(updatedUser)

                // Actualizar fecha de cambio
                val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                _uiState.update { it.copy(lastPasswordChange = today) }

                onSuccess()

                // NO llamar a loadStatistics() aquí - los datos ya están cargados

            } catch (e: Exception) {
                onError("Error al cambiar contraseña: ${e.message}")
            }
        }
    }

    /**
     * Cierra la sesión del usuario.
     * Limpia COMPLETAMENTE todos los datos locales.
     */
    fun logout() {
        viewModelScope.launch {
            // Limpiar DataStore
            userPreferences.clearUser()

            // Limpiar sesión
            UserSession.logout()

            // Limpiar historial local
            historyViewModel.clear()

            // Resetear UI state
            _uiState.value = ProfileUiState()
        }
    }
}