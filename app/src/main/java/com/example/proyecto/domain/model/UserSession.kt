package com.example.proyecto.domain.model

import com.example.proyecto.data.local.UserEntity

/**
 * Sesión del usuario actual.
 * Singleton que mantiene los datos del usuario logueado.
 */
object UserSession {
    private var _currentUser: UserEntity? = null

    val currentUser: UserEntity?
        get() = _currentUser

    val userId: Long
        get() = _currentUser?.id ?: 1L // Default 1L si no hay usuario

    val isLoggedIn: Boolean
        get() = _currentUser != null

    fun login(user: UserEntity) {
        _currentUser = user
    }

    fun logout() {
        _currentUser = null
    }
}