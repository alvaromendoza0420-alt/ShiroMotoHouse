package com.example.shiromotohouse.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_residuos")
data class RegistroResiduo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipoResiduo: String, // "Aceite Usado", "Batería", "Neumático", "Filtro"
    val cantidad: Double,    // Litros o unidades
    val unidad: String,      // "Litros", "Unidades", "Kg"
    val fecha: Long,         // Timestamp
    val observaciones: String,
    val reciclado: Boolean = false // Si ya fue entregado a reciclaje
)