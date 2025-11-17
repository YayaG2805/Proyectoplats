package com.example.proyecto.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_ID = longPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_LASTNAME = stringPreferencesKey("user_lastname")
        private val USER_EMAIL = stringPreferencesKey("user_email")
    }

    val userId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID]
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME] ?: ""
    }

    val userLastName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_LASTNAME] ?: ""
    }

    val userEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_EMAIL] ?: ""
    }

    suspend fun saveUser(id: Long, nombre: String, apellido: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = id
            prefs[USER_NAME] = nombre
            prefs[USER_LASTNAME] = apellido
            prefs[USER_EMAIL] = email
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_ID)
            prefs.remove(USER_NAME)
            prefs.remove(USER_LASTNAME)
            prefs.remove(USER_EMAIL)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        var loggedIn = false
        context.dataStore.data.collect { prefs ->
            loggedIn = prefs[USER_ID] != null
        }
        return loggedIn
    }
}