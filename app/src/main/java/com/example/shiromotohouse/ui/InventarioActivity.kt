package com.example.shiromotohouse.ui

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shiromotohouse.R
import com.example.shiromotohouse.data.local.AppDatabase
import com.example.shiromotohouse.data.local.Repuesto
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InventarioActivity : AppCompatActivity() {

    private lateinit var rvInventario: RecyclerView
    private lateinit var tvVacio: TextView
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var btnExportarPDF: Button
    private lateinit var adapter: RepuestoAdapter
    private lateinit var db: AppDatabase

    // Lista actual de repuestos (para el PDF)
    private var listaRepuestos: List<Repuesto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        db = AppDatabase.obtenerBaseDatos(this)

        rvInventario = findViewById(R.id.rvInventario)
        tvVacio = findViewById(R.id.tvVacio)
        fabAgregar = findViewById(R.id.fabAgregar)
        btnExportarPDF = findViewById(R.id.btnExportarPDF)

        adapter = RepuestoAdapter(
            lista = emptyList(),
            onClick = { },
            onEditar = { repuesto ->
                val intent = Intent(this, RegistroRepuestoActivity::class.java)
                intent.putExtra("MODO_EDICION", true)
                intent.putExtra("REPUESTO_ID", repuesto.id_repuesto)
                intent.putExtra("REPUESTO_NOMBRE", repuesto.nombre)
                intent.putExtra("REPUESTO_CATEGORIA", repuesto.categoria)
                intent.putExtra("REPUESTO_STOCK_ACTUAL", repuesto.stock_actual)
                intent.putExtra("REPUESTO_STOCK_MINIMO", repuesto.stock_minimo)
                intent.putExtra("REPUESTO_PRECIO", repuesto.precio_unitario)
                startActivity(intent)
            },
            onEliminar = { repuesto ->
                lifecycleScope.launch {
                    db.repuestoDao().eliminar(repuesto)
                    Toast.makeText(this@InventarioActivity, "'${repuesto.nombre}' eliminado", Toast.LENGTH_SHORT).show()
                }
            }
        )
        rvInventario.layoutManager = LinearLayoutManager(this)
        rvInventario.adapter = adapter

        fabAgregar.setOnClickListener {
            startActivity(Intent(this, RegistroRepuestoActivity::class.java))
        }

        // Botón para exportar a PDF
        btnExportarPDF.setOnClickListener {
            if (listaRepuestos.isEmpty()) {
                Toast.makeText(this, "No hay repuestos para exportar", Toast.LENGTH_SHORT).show()
            } else {
                generarPDF(listaRepuestos)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            db.repuestoDao().obtenerTodos().collect { lista ->
                listaRepuestos = lista // Guardamos la lista actual
                if (lista.isEmpty()) {
                    tvVacio.visibility = TextView.VISIBLE
                    rvInventario.visibility = RecyclerView.GONE
                } else {
                    tvVacio.visibility = TextView.GONE
                    rvInventario.visibility = RecyclerView.VISIBLE
                    adapter.actualizarLista(lista)
                }
            }
        }
    }

    // ==========================================
    //  LÓGICA DE GENERACIÓN DE PDF (RF08)
    // ==========================================
    private fun generarPDF(repuestos: List<Repuesto>) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Tamaño A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        // Título
        paint.color = Color.BLACK
        paint.textSize = 20f
        paint.isFakeBoldText = true
        canvas.drawText("ShiroMotoHouse - Reporte de Inventario", 40f, 60f, paint)

        // Fecha
        paint.textSize = 12f
        paint.isFakeBoldText = false
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Fecha de generación: $fecha", 40f, 85f, paint)

        // Encabezados de la tabla
        paint.isFakeBoldText = true
        paint.textSize = 12f
        val yInicial = 130f
        canvas.drawText("Código", 40f, yInicial, paint)
        canvas.drawText("Nombre", 140f, yInicial, paint)
        canvas.drawText("Categoría", 300f, yInicial, paint)
        canvas.drawText("Stock", 420f, yInicial, paint)
        canvas.drawText("Precio", 480f, yInicial, paint)

        // Línea separadora
        paint.strokeWidth = 1f
        canvas.drawLine(40f, yInicial + 5f, 555f, yInicial + 5f, paint)

        // Filas de datos
        paint.isFakeBoldText = false
        paint.textSize = 11f
        var y = yInicial + 25f
        for (rep in repuestos) {
            canvas.drawText(rep.id_repuesto, 40f, y, paint)
            canvas.drawText(rep.nombre.take(20), 140f, y, paint)
            canvas.drawText(rep.categoria.take(15), 300f, y, paint)
            canvas.drawText(rep.stock_actual.toString(), 420f, y, paint)
            canvas.drawText("S/ %.2f".format(rep.precio_unitario), 480f, y, paint)
            y += 20f
            if (y > 800f) break // Evitamos salir de la página
        }

        pdfDocument.finishPage(page)

        // Guardamos el PDF en el almacenamiento del dispositivo
        val archivo = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Reporte_Inventario.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(archivo))
            pdfDocument.close()
            Toast.makeText(this, "PDF generado correctamente", Toast.LENGTH_LONG).show()
            compartirPDF(archivo)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun compartirPDF(archivo: File) {
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            archivo
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Compartir Reporte PDF"))
    }
}