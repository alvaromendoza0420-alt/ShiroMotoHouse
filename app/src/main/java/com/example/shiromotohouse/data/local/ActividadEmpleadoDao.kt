package com.example.shiromotohouse.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadEmpleadoDao {
    @Query("SELECT * FROM actividades_empleados ORDER BY fecha DESC")
    fun obtenerTodas(): Flow<List<ActividadEmpleado>>

    @Query("SELECT * FROM actividades_empleados WHERE id_empleado = :idEmpleado")
    fun obtenerPorEmpleado(idEmpleado: String): Flow<List<ActividadEmpleado>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(actividad: ActividadEmpleado)

    @Update
    suspend fun actualizar(actividad: ActividadEmpleado)

    @Delete
    suspend fun eliminar(actividad: ActividadEmpleado)
}