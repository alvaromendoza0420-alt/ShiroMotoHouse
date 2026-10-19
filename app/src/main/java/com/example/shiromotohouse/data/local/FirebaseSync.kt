package com.example.shiromotohouse.data.local

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseSync {
    private val db = FirebaseFirestore.getInstance()

    // Sincronizar Repuestos
    suspend fun sincronizarRepuestos(repuestos: List<Repuesto>) {
        try {
            for (rep in repuestos) {
                db.collection("repuestos").document(rep.id_repuesto).set(rep).await()
            }
            Log.d("FirebaseSync", "Repuestos sincronizados: ${repuestos.size}")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error al sincronizar repuestos: ${e.message}")
        }
    }

    // Sincronizar Motos
    suspend fun sincronizarMotos(motos: List<MotoCliente>) {
        try {
            for (moto in motos) {
                db.collection("motos_clientes").document(moto.placa).set(moto).await()
            }
            Log.d("FirebaseSync", "Motos sincronizadas: ${motos.size}")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error al sincronizar motos: ${e.message}")
        }
    }

    // Sincronizar Actividades
    suspend fun sincronizarActividades(actividades: List<ActividadEmpleado>) {
        try {
            for (act in actividades) {
                db.collection("actividades_empleados").document().set(act).await()
            }
            Log.d("FirebaseSync", "Actividades sincronizadas: ${actividades.size}")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error al sincronizar actividades: ${e.message}")
        }
    }

    // Sincronizar Residuos
    suspend fun sincronizarResiduos(residuos: List<RegistroResiduo>) {
        try {
            for (res in residuos) {
                db.collection("registros_residuos").document().set(res).await()
            }
            Log.d("FirebaseSync", "Residuos sincronizados: ${residuos.size}")
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error al sincronizar residuos: ${e.message}")
        }
    }

    // Función maestra que sincroniza todo
    suspend fun sincronizarTodo(
        repuestos: List<Repuesto>,
        motos: List<MotoCliente>,
        actividades: List<ActividadEmpleado>,
        residuos: List<RegistroResiduo>
    ): Boolean {
        return try {
            sincronizarRepuestos(repuestos)
            sincronizarMotos(motos)
            sincronizarActividades(actividades)
            sincronizarResiduos(residuos)
            true
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Error general: ${e.message}")
            false
        }
    }
}