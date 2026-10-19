package com.example.shiromotohouse.ui

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.shiromotohouse.R
import com.example.shiromotohouse.data.local.Repuesto

class RepuestoAdapter(
    private var lista: List<Repuesto>,
    private val onClick: (Repuesto) -> Unit = {},
    private val onEditar: (Repuesto) -> Unit = {},
    private val onEliminar: (Repuesto) -> Unit = {}
) : RecyclerView.Adapter<RepuestoAdapter.RepuestoViewHolder>() {

    class RepuestoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvItemNombre)
        val tvCodigo: TextView = view.findViewById(R.id.tvItemCodigo)
        val tvCategoria: TextView = view.findViewById(R.id.tvItemCategoria)
        val tvStock: TextView = view.findViewById(R.id.tvItemStock)
        val tvPrecio: TextView = view.findViewById(R.id.tvItemPrecio)
        val tvAlertaStock: TextView = view.findViewById(R.id.tvAlertaStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepuestoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repuesto, parent, false)
        return RepuestoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepuestoViewHolder, position: Int) {
        val repuesto = lista[position]
        holder.tvNombre.text = repuesto.nombre
        holder.tvCodigo.text = "Código: ${repuesto.id_repuesto}"
        holder.tvCategoria.text = "Categoría: ${repuesto.categoria}"
        holder.tvStock.text = "Stock: ${repuesto.stock_actual}"
        holder.tvPrecio.text = "S/ %.2f".format(repuesto.precio_unitario)

        // Lógica de Alerta (RF04)
        if (repuesto.stock_actual <= repuesto.stock_minimo) {
            holder.tvAlertaStock.visibility = View.VISIBLE
            holder.tvStock.setTextColor(0xFFFF0000.toInt())
        } else {
            holder.tvAlertaStock.visibility = View.GONE
            holder.tvStock.setTextColor(0xFF333333.toInt())
        }

        // Clic normal -> ver detalles (opcional)
        holder.itemView.setOnClickListener { onClick(repuesto) }

        // Clic LARGO -> mostrar menú de Editar/Eliminar
        holder.itemView.setOnLongClickListener {
            val opciones = arrayOf("Editar", "Eliminar")
            AlertDialog.Builder(holder.itemView.context)
                .setTitle(repuesto.nombre)
                .setItems(opciones) { _, which ->
                    when (which) {
                        0 -> onEditar(repuesto)
                        1 -> {
                            // Confirmación antes de eliminar
                            AlertDialog.Builder(holder.itemView.context)
                                .setTitle("¿Eliminar repuesto?")
                                .setMessage("¿Estás seguro que quieres eliminar '${repuesto.nombre}'?")
                                .setPositiveButton("Sí") { _, _ -> onEliminar(repuesto) }
                                .setNegativeButton("No", null)
                                .show()
                        }
                    }
                }
                .show()
            true
        }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<Repuesto>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}