package com.example.shiromotohouse.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Repuesto::class, MotoCliente::class, ActividadEmpleado::class, Usuario::class, RegistroResiduo::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repuestoDao(): RepuestoDao
    abstract fun motoClienteDao(): MotoClienteDao
    abstract fun actividadEmpleadoDao(): ActividadEmpleadoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun registroResiduoDao(): RegistroResiduoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerBaseDatos(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shiromotohouse_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}