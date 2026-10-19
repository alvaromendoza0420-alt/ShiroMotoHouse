package com.example.shiromotohouse.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MotoClienteDao {
    @Query("SELECT * FROM motos_clientes ORDER BY propietario ASC")
    fun obtenerTodas(): Flow<List<MotoCliente>>

    @Query("SELECT * FROM motos_clientes WHERE placa = :placa")
    suspend fun obtenerPorPlaca(placa: String): MotoCliente?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(moto: MotoCliente)

    @Update
    suspend fun actualizar(moto: MotoCliente)

    @Delete
    suspend fun eliminar(moto: MotoCliente)
}