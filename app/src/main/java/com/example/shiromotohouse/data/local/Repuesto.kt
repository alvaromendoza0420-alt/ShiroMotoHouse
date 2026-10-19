package com.example.shiromotohouse.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repuestos")
data class Repuesto(
    @PrimaryKey
    val id_repuesto: String, // El ID será el código QR escaneado (String)
    val nombre: String,
    val categoria: String,
    val stock_actual: Int,
    val stock_minimo: Int,
    val precio_unitario: Double
)