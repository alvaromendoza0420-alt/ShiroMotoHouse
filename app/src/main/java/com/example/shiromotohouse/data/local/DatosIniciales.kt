package com.example.shiromotohouse.data.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatosIniciales {
    fun precargarUsuarios(db: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            if (db.usuarioDao().contarUsuarios() == 0) {
                db.usuarioDao().insertar(Usuario("admin", "123", "Administrador General", "Administrador"))
                db.usuarioDao().insertar(Usuario("mecanico1", "123", "Juan Pérez", "Mecánico"))
                db.usuarioDao().insertar(Usuario("almacen1", "123", "María López", "Almacenero"))
            }
        }
    }
}