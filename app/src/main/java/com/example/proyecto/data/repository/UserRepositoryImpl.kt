package com.example.proyecto.data.repository

import com.example.proyecto.data.local.UserDao
import com.example.proyecto.data.local.UserEntity

class UserRepositoryImpl(
    private val dao: UserDao
) : UserRepository {

    override suspend fun register(
        nombre: String,
        apellido: String,
        email: String,
        password: String
    ): Result<Long> = try {
        val existing = dao.getByEmail(email)
        if (existing != null) {
            Result.failure(IllegalStateException("Ya existe un usuario con ese correo"))
        } else {
            val id = dao.insert(
                UserEntity(
                    nombre = nombre,
                    apellido = apellido,
                    email = email,
                    password = password
                )
            )
            Result.success(id)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<UserEntity> = try {
        val user = dao.getByEmail(email)
        if (user == null || user.password != password) {
            Result.failure(IllegalArgumentException("Correo o contraseña incorrectos"))
        } else {
            Result.success(user)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
