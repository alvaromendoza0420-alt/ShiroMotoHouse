package com.example.shiromotohouse.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shiromotohouse.R
import com.example.shiromotohouse.data.local.ActividadEmpleado
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActividadAdapter(
    private var lista: List<ActividadEmpleado>
) : RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder>() {

    class ActividadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTipo: TextView = view.findViewById(R.id.tvItemTipoActividad)
        val tvEmpleado: TextView = view.findViewById(R.id.tvItemEmpleado)
        val tvPlaca: TextView = view.findViewById(R.id.tvItemPlacaActividad)
        val tvFecha: TextView = view.findViewById(R.id.tvItemFechaActividad)
        val tvEstado: TextView = view.findViewById(R.id.tvItemEstadoActividad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_actividad, parent, false)
        return ActividadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = lista[position]
        holder.tvTipo.text = actividad.tipo_actividad
        holder.tvEmpleado.text = "Empleado: ${actividad.id_empleado}"
        holder.tvPlaca.text = "Moto: ${actividad.placa_moto_asociada}"

        // Formatear la fecha
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fechaFormateada = formato.format(Date(actividad.fecha))
        holder.tvFecha.text = "Fecha: $fechaFormateada"

        holder.tvEstado.text = actividad.estado
        if (actividad.estado == "Finalizado") {
            holder.tvEstado.setTextColor(0xFF4CAF50.toInt()) // Verde
        } else {
            holder.tvEstado.setTextColor(0xFFFF9800.toInt()) // Naranja
        }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<ActividadEmpleado>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}