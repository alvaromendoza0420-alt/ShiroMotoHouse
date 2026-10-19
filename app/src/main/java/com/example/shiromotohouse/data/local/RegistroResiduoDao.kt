package com.example.shiromotohouse.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroResiduoDao {
    @Query("SELECT * FROM registros_residuos ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<RegistroResiduo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(registro: RegistroResiduo)

    @Delete
    suspend fun eliminar(registro: RegistroResiduo)

    @Query("SELECT SUM(cantidad) FROM registros_residuos WHERE tipoResiduo = :tipo")
    suspend fun obtenerTotalPorTipo(tipo: String): Double?
}