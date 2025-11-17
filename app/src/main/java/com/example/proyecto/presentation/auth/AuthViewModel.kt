package com.example.proyecto.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.UserPreferences
import com.example.proyecto.data.repository.UserRepository
import com.example.proyecto.domain.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val loginEmail: String = "",
    val loginPassword: String = "",
    val registerNombre: String = "",
    val registerApellido: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val registerPassword2: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val registerSuccess: Boolean = false
)

class AuthViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    // === Eventos de cambio de texto ===
    fun onLoginEmailChange(value: String) {
        _ui.update { it.copy(loginEmail = value, error = null) }
    }

    fun onLoginPasswordChange(value: String) {
        _ui.update { it.copy(loginPassword = value, error = null) }
    }

    fun onRegisterNombreChange(value: String) {
        _ui.update { it.copy(registerNombre = value, error = null) }
    }

    fun onRegisterApellidoChange(value: String) {
        _ui.update { it.copy(registerApellido = value, error = null) }
    }

    fun onRegisterEmailChange(value: String) {
        _ui.update { it.copy(registerEmail = value, error = null) }
    }

    fun onRegisterPasswordChange(value: String) {
        _ui.update { it.copy(registerPassword = value, error = null) }
    }

    fun onRegisterPassword2Change(value: String) {
        _ui.update { it.copy(registerPassword2 = value, error = null) }
    }

    // === Acciones ===

    fun login() {
        val current = _ui.value
        if (current.loginEmail.isBlank() || current.loginPassword.isBlank()) {
            _ui.update { it.copy(error = "Correo y contraseña son obligatorios") }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }

            // ===== FIX: Limpiar sesión anterior ANTES de login =====
            UserSession.logout()
            userPreferences.clearUser()

            val result = userRepository.login(current.loginEmail, current.loginPassword)
            result.fold(
                onSuccess = { user ->
                    // Guardar en sesión
                    UserSession.login(user)
                    _ui.update { it.copy(loading = false, loginSuccess = true) }
                },
                onFailure = { e ->
                    _ui.update { it.copy(loading = false, error = e.message ?: "Error al iniciar sesión") }
                }
            )
        }
    }

    fun register() {
        val current = _ui.value

        if (current.registerNombre.isBlank() ||
            current.registerApellido.isBlank() ||
            current.registerEmail.isBlank() ||
            current.registerPassword.isBlank()
        ) {
            _ui.update { it.copy(error = "Todos los campos son obligatorios") }
            return
        }

        if (current.registerPassword != current.registerPassword2) {
            _ui.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }

            // ===== FIX: Limpiar sesión anterior ANTES de registro =====
            UserSession.logout()
            userPreferences.clearUser()

            val result = userRepository.register(
                nombre = current.registerNombre,
                apellido = current.registerApellido,
                email = current.registerEmail,
                password = current.registerPassword
            )
            result.fold(
                onSuccess = { userId ->
                    // Crear sesión automáticamente
                    val user = userRepository.getUserById(userId)
                    if (user != null) {
                        UserSession.login(user)
                    }
                    _ui.update { it.copy(loading = false, registerSuccess = true) }
                },
                onFailure = { e ->
                    _ui.update { it.copy(loading = false, error = e.message ?: "Error al registrar usuario") }
                }
            )
        }
    }

    fun clearNavigationFlags() {
        _ui.update { it.copy(loginSuccess = false, registerSuccess = false) }
    }
}