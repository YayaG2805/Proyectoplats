package com.example.proyecto.data.repository

import com.example.proyecto.data.local.UserEntity

interface UserRepository {

    suspend fun register(
        nombre: String,
        apellido: String,
        email: String,
        password: String
    ): Result<Long>

    suspend fun login(
        email: String,
        password: String
    ): Result<UserEntity>

    suspend fun getUserById(userId: Long): UserEntity?
}