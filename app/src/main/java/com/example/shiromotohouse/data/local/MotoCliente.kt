package com.example.shiromotohouse.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motos_clientes")
data class MotoCliente(
    @PrimaryKey
    val placa: String,
    val propietario: String,
    val telefono: String,
    val modelo: String
    // El historial de servicios lo manejaremos con otra tabla o una relación más adelante
    // para mantenerlo simple por ahora según el informe.
)
