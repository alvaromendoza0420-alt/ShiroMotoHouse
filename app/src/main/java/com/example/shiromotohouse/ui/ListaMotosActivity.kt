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

class ListaMotosActivity : AppCompatActivity() {

    private lateinit var rvMotos: RecyclerView
    private lateinit var tvVacioMotos: TextView
    private lateinit var fabAgregarMoto: FloatingActionButton
    private lateinit var adapter: MotoAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_motos)

        db = AppDatabase.obtenerBaseDatos(this)

        rvMotos = findViewById(R.id.rvMotos)
        tvVacioMotos = findViewById(R.id.tvVacioMotos)
        fabAgregarMoto = findViewById(R.id.fabAgregarMoto)

        adapter = MotoAdapter(emptyList())
        rvMotos.layoutManager = LinearLayoutManager(this)
        rvMotos.adapter = adapter

        fabAgregarMoto.setOnClickListener {
            val intent = Intent(this, RegistroMotoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            db.motoClienteDao().obtenerTodas().collect { lista ->
                if (lista.isEmpty()) {
                    tvVacioMotos.visibility = TextView.VISIBLE
                    rvMotos.visibility = RecyclerView.GONE
                } else {
                    tvVacioMotos.visibility = TextView.GONE
                    rvMotos.visibility = RecyclerView.VISIBLE
                    adapter.actualizarLista(lista)
                }
            }
        }
    }
}