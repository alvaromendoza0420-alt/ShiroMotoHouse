package com.example.shiromotohouse.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shiromotohouse.R
import com.example.shiromotohouse.data.local.MotoCliente

class MotoAdapter(
    private var lista: List<MotoCliente>,
    private val onClick: (MotoCliente) -> Unit = {}
) : RecyclerView.Adapter<MotoAdapter.MotoViewHolder>() {

    class MotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlaca: TextView = view.findViewById(R.id.tvItemPlaca)
        val tvPropietario: TextView = view.findViewById(R.id.tvItemPropietario)
        val tvTelefono: TextView = view.findViewById(R.id.tvItemTelefono)
        val tvModelo: TextView = view.findViewById(R.id.tvItemModelo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_moto, parent, false)
        return MotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MotoViewHolder, position: Int) {
        val moto = lista[position]
        holder.tvPlaca.text = moto.placa
        holder.tvPropietario.text = "Propietario: ${moto.propietario}"
        holder.tvTelefono.text = "Teléfono: ${moto.telefono}"
        holder.tvModelo.text = "Modelo: ${moto.modelo}"

        holder.itemView.setOnClickListener { onClick(moto) }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<MotoCliente>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}