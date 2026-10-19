package com.example.shiromotohouse.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RepuestoDao {
    @Query("SELECT * FROM repuestos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Repuesto>>

    @Query("SELECT * FROM repuestos WHERE id_repuesto = :id")
    suspend fun obtenerPorId(id: String): Repuesto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(repuesto: Repuesto)

    @Update
    suspend fun actualizar(repuesto: Repuesto)

    @Delete
    suspend fun eliminar(repuesto: Repuesto)

    // Consulta clave para las alertas (RF04)
    @Query("SELECT * FROM repuestos WHERE stock_actual <= stock_minimo")
    fun obtenerStockBajo(): Flow<List<Repuesto>>
}