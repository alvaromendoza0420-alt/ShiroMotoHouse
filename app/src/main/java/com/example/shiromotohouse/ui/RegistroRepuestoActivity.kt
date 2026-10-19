package com.example.shiromotohouse.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shiromotohouse.R
import com.example.shiromotohouse.data.local.AppDatabase
import com.example.shiromotohouse.data.local.Repuesto
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RegistroRepuestoActivity : AppCompatActivity() {

    private lateinit var etCodigoQR: EditText
    private lateinit var etNombre: EditText
    private lateinit var etCategoria: EditText
    private lateinit var etStockActual: EditText
    private lateinit var etStockMinimo: EditText
    private lateinit var etPrecio: EditText
    private lateinit var btnGuardar: Button
    private lateinit var tilCodigoQR: TextInputLayout
    private lateinit var db: AppDatabase

    private var modoEdicion = false
    private var codigoOriginal = ""

    private val lanzadorEscaner = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val codigoEscaneado = result.data?.getStringExtra("CODIGO_QR")
            if (!codigoEscaneado.isNullOrEmpty()) {
                etCodigoQR.setText(codigoEscaneado)
                Toast.makeText(this, "Código capturado: $codigoEscaneado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_repuesto)

        db = AppDatabase.obtenerBaseDatos(this)

        // Vinculamos
        tilCodigoQR = findViewById(R.id.tilCodigoQR)
        etCodigoQR = findViewById(R.id.etCodigoQR)
        etNombre = findViewById(R.id.etNombre)
        etCategoria = findViewById(R.id.etCategoria)
        etStockActual = findViewById(R.id.etStockActual)
        etStockMinimo = findViewById(R.id.etStockMinimo)
        etPrecio = findViewById(R.id.etPrecio)
        btnGuardar = findViewById(R.id.btnGuardarRepuesto)

        // Detectar si estamos en modo edición
        modoEdicion = intent.getBooleanExtra("MODO_EDICION", false)
        if (modoEdicion) {
            // Llenamos los campos con los datos existentes
            codigoOriginal = intent.getStringExtra("REPUESTO_ID") ?: ""
            etCodigoQR.setText(codigoOriginal)
            etNombre.setText(intent.getStringExtra("REPUESTO_NOMBRE"))
            etCategoria.setText(intent.getStringExtra("REPUESTO_CATEGORIA"))
            etStockActual.setText(intent.getIntExtra("REPUESTO_STOCK_ACTUAL", 0).toString())
            etStockMinimo.setText(intent.getIntExtra("REPUESTO_STOCK_MINIMO", 0).toString())
            etPrecio.setText(intent.getDoubleExtra("REPUESTO_PRECIO", 0.0).toString())
            btnGuardar.text = "ACTUALIZAR REPUESTO"
        }

        // Recibimos el código QR escaneado (si venimos desde el menú principal)
        val codigoQRRecibido = intent.getStringExtra("CODIGO_QR") ?: ""
        if (codigoQRRecibido.isNotEmpty() && !modoEdicion) {
            etCodigoQR.setText(codigoQRRecibido)
        }

        // Icono de cámara
        tilCodigoQR.setEndIconOnClickListener {
            val intent = Intent(this, EscanerActivity::class.java)
            lanzadorEscaner.launch(intent)
        }

        btnGuardar.setOnClickListener {
            val codigo = etCodigoQR.text.toString().trim()
            val nombre = etNombre.text.toString().trim()
            val categoria = etCategoria.text.toString().trim()
            val stockActualStr = etStockActual.text.toString().trim()
            val stockMinimoStr = etStockMinimo.text.toString().trim()
            val precioStr = etPrecio.text.toString().trim()

            if (codigo.isEmpty() || nombre.isEmpty() || categoria.isEmpty() ||
                stockActualStr.isEmpty() || stockMinimoStr.isEmpty() || precioStr.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val repuesto = Repuesto(
                id_repuesto = codigo,
                nombre = nombre,
                categoria = categoria,
                stock_actual = stockActualStr.toInt(),
                stock_minimo = stockMinimoStr.toInt(),
                precio_unitario = precioStr.toDouble()
            )

            lifecycleScope.launch {
                try {
                    if (modoEdicion) {
                        // Si cambió el código, eliminamos el registro viejo e insertamos el nuevo
                        if (codigoOriginal != codigo) {
                            db.repuestoDao().obtenerPorId(codigoOriginal)?.let {
                                db.repuestoDao().eliminar(it)
                            }
                        }
                        db.repuestoDao().insertar(repuesto)
                        Toast.makeText(this@RegistroRepuestoActivity, "¡Repuesto actualizado!", Toast.LENGTH_LONG).show()
                    } else {
                        db.repuestoDao().insertar(repuesto)
                        Toast.makeText(this@RegistroRepuestoActivity, "¡Repuesto guardado!", Toast.LENGTH_LONG).show()
                    }
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@RegistroRepuestoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}