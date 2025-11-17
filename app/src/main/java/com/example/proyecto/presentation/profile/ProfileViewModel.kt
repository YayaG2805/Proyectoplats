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
    val totalDailyExpenses: Int = 0,
    val totalSavingsPlanned: String = "0",
    val totalIncome: String = "0",
    val totalFixedExpenses: String = "0",
    val totalDailyExpensesAmount: String = "0",
    val savingPercentage: Double = 0.0,
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

    private fun loadUserData() {
        viewModelScope.launch {
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

    private fun loadStatistics() {
        viewModelScope.launch {
            userPreferences.userId.collect { userId ->
                if (userId != null) {
                    combine(
                        monthlyBudgetDao.getAllByUser(userId),
                        dailyExpenseDao.getRecentExpenses(userId)
                    ) { budgets, dailyExpenses ->
                        val totalMonths = budgets.size

                        val totalIncome = budgets.sumOf { it.income }
                        val totalFixedExpenses = budgets.sumOf {
                            it.rent + it.utilities + it.transport + it.other
                        }

                        val totalDailyExpensesAmount = dailyExpenses.sumOf { it.amount }
                        val totalPlannedSavings = totalIncome - totalFixedExpenses

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

                if (user.password != currentPassword) {
                    onError("Contraseña actual incorrecta")
                    return@launch
                }

                if (newPassword.length < 6) {
                    onError("La nueva contraseña debe tener al menos 6 caracteres")
                    return@launch
                }

                val updatedUser = user.copy(password = newPassword)
                userDao.update(updatedUser)

                val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                _uiState.update { it.copy(lastPasswordChange = today) }

                onSuccess()

            } catch (e: Exception) {
                onError("Error al cambiar contraseña: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearUser()
            UserSession.logout()
            historyViewModel.clear()
            _uiState.value = ProfileUiState()
        }
    }
}