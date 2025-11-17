package com.example.proyecto.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.DailyExpenseDao
import com.example.proyecto.presentation.history.HistoryViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Usuario",
    val userEmail: String = "usuario@piggymobile.com",
    val totalMonths: Int = 0,
    val totalExpenses: Int = 0,
    val totalSavings: String = "0",
    val notificationsEnabled: Boolean = true,
    val budgetAlertsEnabled: Boolean = true,
    val lastPasswordChange: String = "Nunca",
    val appVersion: String = "1.0.0"
)

class ProfileViewModel(
    private val dailyExpenseDao: DailyExpenseDao,
    private val historyViewModel: HistoryViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadStatistics()
    }

    /**
     * Carga los datos del usuario desde la base de datos.
     * TODO: En producción, obtener de UserDao con el usuario logueado
     */
    private fun loadUserData() {
        viewModelScope.launch {
            // TODO: Obtener usuario real de la sesión
            // Por ahora usamos datos de ejemplo
            _uiState.update {
                it.copy(
                    userName = "Usuario PiggyMobile",
                    userEmail = "usuario@piggymobile.com",
                    lastPasswordChange = "Hace 2 meses"
                )
            }
        }
    }

    /**
     * Carga estadísticas del usuario desde el historial y gastos.
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            // Obtener número de meses del historial
            historyViewModel.rows.collect { rows ->
                val totalMonths = rows.size

                // Calcular ahorro total (ejemplo: 10% del ingreso)
                val totalSavings = rows.sumOf { row ->
                    // Extraer número del string "Q 7000"
                    row.ahorro.replace("Q ", "").replace(",", "").toDoubleOrNull() ?: 0.0
                }

                _uiState.update {
                    it.copy(
                        totalMonths = totalMonths,
                        totalSavings = String.format("%.0f", totalSavings)
                    )
                }
            }

            // Obtener total de gastos diarios registrados
            // TODO: userId real
            dailyExpenseDao.getRecentExpenses(1L).collect { expenses ->
                _uiState.update {
                    it.copy(totalExpenses = expenses.size)
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
            if (enabled) {
                // TODO: Programar notificaciones
                // AlarmScheduler.scheduleDailyReminder(context)
            } else {
                // TODO: Cancelar notificaciones
                // AlarmScheduler.cancelDailyReminder(context)
            }
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
     * Cierra la sesión del usuario.
     */
    fun logout() {
        viewModelScope.launch {
            // TODO: Limpiar sesión
            // - Borrar token de autenticación
            // - Limpiar SharedPreferences/DataStore
            // - Resetear ViewModels

            // Limpiar historial local
            historyViewModel.clear()
        }
    }
}