package com.example.proyecto.data.repository

import com.example.proyecto.data.local.UserDao
import com.example.proyecto.data.local.UserEntity
import com.example.proyecto.data.local.UserPreferences

class UserRepositoryImpl(
    private val dao: UserDao,
    private val prefs: UserPreferences
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
            // Guardar en DataStore
            prefs.saveUser(id, nombre, apellido, email)
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
            // Guardar en DataStore
            prefs.saveUser(user.id, user.nombre, user.apellido, user.email)
            Result.success(user)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getUserById(userId: Long): UserEntity? {
        return dao.getById(userId)
    }
}