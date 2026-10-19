package com.example.shiromotohouse.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey
    val username: String,
    val password: String, // En un proyecto real se encriptaría, para fines académicos la dejamos así
    val nombreCompleto: String,
    val rol: String // "Administrador", "Mecánico", "Almacenero"
)