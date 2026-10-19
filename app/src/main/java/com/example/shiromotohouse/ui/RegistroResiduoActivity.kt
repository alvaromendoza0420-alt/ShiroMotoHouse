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
import com.example.shiromotohouse.data.local.AppDatabase
import com.example.shiromotohouse.data.local.RegistroResiduo
import kotlinx.coroutines.launch

class RegistroResiduoActivity : AppCompatActivity() {

    private lateinit var actTipoResiduo: AutoCompleteTextView
    private lateinit var etCantidad: EditText
    private lateinit var etObservaciones: EditText
    private lateinit var btnGuardar: Button
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_residuo)

        db = AppDatabase.obtenerBaseDatos(this)

        actTipoResiduo = findViewById(R.id.actTipoResiduo)
        etCantidad = findViewById(R.id.etCantidad)
        etObservaciones = findViewById(R.id.etObservaciones)
        btnGuardar = findViewById(R.id.btnGuardarResiduo)

        // Tipos de residuos según ODS 12
        val tipos = arrayOf("Aceite Usado", "Batería", "Neumático", "Filtro de Aceite", "Filtro de Aire")
        actTipoResiduo.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tipos))

        // Cambiamos la unidad automáticamente según el tipo
        actTipoResiduo.setOnItemClickListener { _, _, _, _ ->
            val tipo = actTipoResiduo.text.toString()
            val hint = if (tipo == "Aceite Usado") "Cantidad (Litros)" else "Cantidad (Unidades)"
            findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilCantidad).hint = hint
        }

        btnGuardar.setOnClickListener {
            val tipo = actTipoResiduo.text.toString().trim()
            val cantidadStr = etCantidad.text.toString().trim()
            val observaciones = etObservaciones.text.toString().trim()

            if (tipo.isEmpty() || cantidadStr.isEmpty()) {
                Toast.makeText(this, "Complete el tipo y la cantidad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val unidad = if (tipo == "Aceite Usado") "Litros" else "Unidades"

            val registro = RegistroResiduo(
                tipoResiduo = tipo,
                cantidad = cantidadStr.toDouble(),
                unidad = unidad,
                fecha = System.currentTimeMillis(),
                observaciones = if (observaciones.isEmpty()) "Sin observaciones" else observaciones,
                reciclado = false
            )

            lifecycleScope.launch {
                try {
                    db.registroResiduoDao().insertar(registro)
                    Toast.makeText(this@RegistroResiduoActivity, "¡Registro guardado!", Toast.LENGTH_LONG).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@RegistroResiduoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}