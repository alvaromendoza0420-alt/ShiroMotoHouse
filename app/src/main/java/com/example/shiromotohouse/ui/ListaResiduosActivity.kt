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
import com.example.shiromotohouse.data.local.RegistroResiduo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ListaResiduosActivity : AppCompatActivity() {

    private lateinit var rvResiduos: RecyclerView
    private lateinit var tvVacio: TextView
    private lateinit var tvResumen: TextView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var adapter: ResiduoAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_residuos)

        db = AppDatabase.obtenerBaseDatos(this)

        rvResiduos = findViewById(R.id.rvResiduos)
        tvVacio = findViewById(R.id.tvVacioResiduos)
        tvResumen = findViewById(R.id.tvResumenTotales)
        fabAgregar = findViewById(R.id.fabAgregarResiduo)

        adapter = ResiduoAdapter(emptyList())
        rvResiduos.layoutManager = LinearLayoutManager(this)
        rvResiduos.adapter = adapter

        fabAgregar.setOnClickListener {
            startActivity(Intent(this, RegistroResiduoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            db.registroResiduoDao().obtenerTodos().collect { lista ->
                if (lista.isEmpty()) {
                    tvVacio.visibility = TextView.VISIBLE
                    rvResiduos.visibility = RecyclerView.GONE
                } else {
                    tvVacio.visibility = TextView.GONE
                    rvResiduos.visibility = RecyclerView.VISIBLE
                    adapter.actualizarLista(lista)
                    actualizarResumen(lista)
                }
            }
        }
    }

    private fun actualizarResumen(lista: List<RegistroResiduo>) {
        val totalAceite = lista.filter { it.tipoResiduo == "Aceite Usado" }.sumOf { it.cantidad }
        val totalBaterias = lista.filter { it.tipoResiduo == "Batería" }.sumOf { it.cantidad }
        val totalNeumaticos = lista.filter { it.tipoResiduo == "Neumático" }.sumOf { it.cantidad }

        tvResumen.text = "🌱 Resumen ODS 12:\n" +
                "• Aceite reciclado: $totalAceite Litros\n" +
                "• Baterías gestionadas: $totalBaterias Unidades\n" +
                "• Neumáticos gestionados: $totalNeumaticos Unidades"
    }
}