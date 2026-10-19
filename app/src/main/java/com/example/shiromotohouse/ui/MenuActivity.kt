package com.example.shiromotohouse.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shiromotohouse.R
import com.example.shiromotohouse.data.local.AppDatabase
import com.example.shiromotohouse.data.local.FirebaseSync
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // ... dentro de onCreate, después de setContentView:
        val tvFecha: TextView = findViewById(R.id.tvFecha)
        val fechaActual = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        tvFecha.text = fechaActual

        db = AppDatabase.obtenerBaseDatos(this)

        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val tvBienvenida: TextView = findViewById(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $nombreUsuario"

        // Navegación de los botones principales
        findViewById<MaterialCardView>(R.id.cardEscanear).setOnClickListener {
            val intent = Intent(this, ListaActividadesActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            startActivity(intent)
        }
        findViewById<MaterialCardView>(R.id.cardInventario).setOnClickListener {
            startActivity(Intent(this, InventarioActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.cardMotos).setOnClickListener {
            startActivity(Intent(this, ListaMotosActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.cardODS).setOnClickListener {
            startActivity(Intent(this, ListaResiduosActivity::class.java))
        }

        // BOTÓN DE SINCRONIZAR (NUBE)
        findViewById<FloatingActionButton>(R.id.fabSincronizar).setOnClickListener {
            sincronizarConFirebase()
        }
    }

    private fun sincronizarConFirebase() {
        Toast.makeText(this, "Sincronizando datos...", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            try {
                // Obtenemos los datos locales de Room
                val repuestos = db.repuestoDao().obtenerTodos().first()
                val motos = db.motoClienteDao().obtenerTodas().first()
                val actividades = db.actividadEmpleadoDao().obtenerTodas().first()
                val residuos = db.registroResiduoDao().obtenerTodos().first()

                // Sincronizamos con Firebase
                val exito = FirebaseSync.sincronizarTodo(repuestos, motos, actividades, residuos)

                if (exito) {
                    Toast.makeText(this@MenuActivity, "¡Datos sincronizados con la nube!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MenuActivity, "Error al sincronizar. Revisa tu conexión.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MenuActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}