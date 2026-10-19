package com.example.shiromotohouse.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shiromotohouse.R
import com.example.shiromotohouse.data.local.AppDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ListaActividadesActivity : AppCompatActivity() {

    private lateinit var rvActividades: RecyclerView
    private lateinit var tvVacioActividades: TextView
    private lateinit var fabAgregarActividad: FloatingActionButton
    private lateinit var adapter: ActividadAdapter
    private lateinit var db: AppDatabase

    private var nombreUsuario: String = "Mecánico"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_actividades)

        db = AppDatabase.obtenerBaseDatos(this)
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Mecánico"

        rvActividades = findViewById(R.id.rvActividades)
        tvVacioActividades = findViewById(R.id.tvVacioActividades)
        fabAgregarActividad = findViewById(R.id.fabAgregarActividad)

        adapter = ActividadAdapter(emptyList())
        rvActividades.layoutManager = LinearLayoutManager(this)
        rvActividades.adapter = adapter

        fabAgregarActividad.setOnClickListener {
            val intent = Intent(this, RegistroActividadActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            db.actividadEmpleadoDao().obtenerTodas().collect { lista ->
                if (lista.isEmpty()) {
                    tvVacioActividades.visibility = TextView.VISIBLE
                    rvActividades.visibility = RecyclerView.GONE
                } else {
                    tvVacioActividades.visibility = TextView.GONE
                    rvActividades.visibility = RecyclerView.VISIBLE
                    adapter.actualizarLista(lista)
                }
            }
        }
    }
}