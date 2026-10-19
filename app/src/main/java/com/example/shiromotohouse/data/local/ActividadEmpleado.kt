package com.example.shiromotohouse.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividades_empleados")
data class ActividadEmpleado(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID autoincremental para cada actividad
    val id_empleado: String,
    val fecha: Long, // Guardaremos la fecha como Long (timestamp)
    val tipo_actividad: String,
    val placa_moto_asociada: String,
    val estado: String // Ejemplo: "En proceso", "Finalizado"
)
