package com.example.shiromotohouse.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shiromotohouse.R
import com.example.shiromotohouse.data.local.AppDatabase
import com.example.shiromotohouse.data.local.MotoCliente
import kotlinx.coroutines.launch

class RegistroMotoActivity : AppCompatActivity() {

    private lateinit var etPlaca: EditText
    private lateinit var etPropietario: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etModelo: EditText
    private lateinit var btnGuardar: Button
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_moto)

        db = AppDatabase.obtenerBaseDatos(this)

        etPlaca = findViewById(R.id.etPlaca)
        etPropietario = findViewById(R.id.etPropietario)
        etTelefono = findViewById(R.id.etTelefono)
        etModelo = findViewById(R.id.etModelo)
        btnGuardar = findViewById(R.id.btnGuardarMoto)

        btnGuardar.setOnClickListener {
            val placa = etPlaca.text.toString().trim().uppercase()
            val propietario = etPropietario.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val modelo = etModelo.text.toString().trim()

            if (placa.isEmpty() || propietario.isEmpty() || telefono.isEmpty() || modelo.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val moto = MotoCliente(
                placa = placa,
                propietario = propietario,
                telefono = telefono,
                modelo = modelo
            )

            lifecycleScope.launch {
                try {
                    db.motoClienteDao().insertar(moto)
                    Toast.makeText(this@RegistroMotoActivity, "¡Moto registrada!", Toast.LENGTH_LONG).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@RegistroMotoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}