package com.example.shiromotohouse.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shiromotohouse.R
import com.example.shiromotohouse.data.local.RegistroResiduo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResiduoAdapter(
    private var lista: List<RegistroResiduo>
) : RecyclerView.Adapter<ResiduoAdapter.ResiduoViewHolder>() {

    class ResiduoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTipo: TextView = view.findViewById(R.id.tvItemTipoResiduo)
        val tvCantidad: TextView = view.findViewById(R.id.tvItemCantidadResiduo)
        val tvFecha: TextView = view.findViewById(R.id.tvItemFechaResiduo)
        val tvObs: TextView = view.findViewById(R.id.tvItemObsResiduo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResiduoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_residuo, parent, false)
        return ResiduoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResiduoViewHolder, position: Int) {
        val residuo = lista[position]
        holder.tvTipo.text = residuo.tipoResiduo
        holder.tvCantidad.text = "Cantidad: ${residuo.cantidad} ${residuo.unidad}"

        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.tvFecha.text = "Fecha: ${formato.format(Date(residuo.fecha))}"
        holder.tvObs.text = "Obs: ${residuo.observaciones}"
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<RegistroResiduo>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}