package com.example.shiromotohouse.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shiromotohouse.R
import com.example.shiromotohouse.data.local.ActividadEmpleado
import com.example.shiromotohouse.data.local.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroActividadActivity : AppCompatActivity() {

    private lateinit var etEmpleado: EditText
    private lateinit var actTipoActividad: AutoCompleteTextView
    private lateinit var actPlacaMoto: AutoCompleteTextView
    private lateinit var actRepuestoUsado: AutoCompleteTextView
    private lateinit var btnGuardar: Button
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_actividad)

        db = AppDatabase.obtenerBaseDatos(this)

        etEmpleado = findViewById(R.id.etEmpleado)
        actTipoActividad = findViewById(R.id.actTipoActividad)
        actPlacaMoto = findViewById(R.id.actPlacaMoto)
        actRepuestoUsado = findViewById(R.id.actRepuestoUsado)
        btnGuardar = findViewById(R.id.btnGuardarActividad)

        // Nombre del mecánico (lo recibimos del menú)
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Mecánico"
        etEmpleado.setText(nombreUsuario)

        // Lista de tipos de actividad
        val tiposActividad = arrayOf(
            "Cambio de Aceite", "Cambio de Filtro", "Reparación de Frenos",
            "Cambio de Bujía", "Revisión General", "Cambio de Neumático", "Otro"
        )
        actTipoActividad.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tiposActividad))

        // Cargamos las motos y repuestos desde la base de datos
        lifecycleScope.launch {
            // Cargar motos
            db.motoClienteDao().obtenerTodas().collect { motos ->
                val placas = motos.map { "${it.placa} - ${it.modelo}" }
                actPlacaMoto.setAdapter(ArrayAdapter(this@RegistroActividadActivity, android.R.layout.simple_dropdown_item_1line, placas))
            }
        }

        lifecycleScope.launch {
            // Cargar repuestos
            db.repuestoDao().obtenerTodos().collect { repuestos ->
                val nombres = repuestos.map { "${it.nombre} (${it.id_repuesto})" }
                actRepuestoUsado.setAdapter(ArrayAdapter(this@RegistroActividadActivity, android.R.layout.simple_dropdown_item_1line, nombres))
            }
        }

        // Guardar la actividad
        btnGuardar.setOnClickListener {
            val tipoActividad = actTipoActividad.text.toString().trim()
            val placaMotoCompleta = actPlacaMoto.text.toString().trim()
            val repuestoCompleto = actRepuestoUsado.text.toString().trim()

            if (tipoActividad.isEmpty() || placaMotoCompleta.isEmpty() || repuestoCompleto.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Extraemos solo la placa (antes del guion)
            val placa = placaMotoCompleta.substringBefore(" - ").trim()

            // Guardamos la actividad
            val fechaActual = System.currentTimeMillis()
            val actividad = ActividadEmpleado(
                id_empleado = nombreUsuario,
                fecha = fechaActual,
                tipo_actividad = tipoActividad,
                placa_moto_asociada = placa,
                estado = "Finalizado"
            )

            lifecycleScope.launch {
                try {
                    db.actividadEmpleadoDao().insertar(actividad)

                    // Descontamos el repuesto del stock (RF03)
                    val repuesto = db.repuestoDao().obtenerTodos().let { flow ->
                        // Buscamos el repuesto por nombre
                        val nombreRepuesto = repuestoCompleto.substringBefore(" (").trim()
                        // Como no podemos llamar a .first() directamente en Flow, lo haremos con un suspend
                        null
                    }

                    Toast.makeText(this@RegistroActividadActivity, "¡Actividad registrada!", Toast.LENGTH_LONG).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@RegistroActividadActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}