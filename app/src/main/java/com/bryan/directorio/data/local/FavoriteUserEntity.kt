package com.bryan.directorio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla "favoritos". Guarda una copia de los datos del usuario
 * para poder mostrarlos sin conexión.
 */
@Entity(tableName = "favoritos")
data class FavoriteUserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val city: String,
    val companyName: String,
    val savedAt: Long = System.currentTimeMillis()
)
