package pe.edu.idat.idatparking.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pe.edu.idat.idatparking.R
import pe.edu.idat.idatparking.entity.MovimientoHistorial

class MovimientoAdapter(
    private var movimientos: List<MovimientoHistorial>
) : RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder>() {

    class MovimientoViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtPlaca: TextView =
            itemView.findViewById(
                R.id.txtPlacaMovimiento
            )

        val txtUsuario: TextView =
            itemView.findViewById(
                R.id.txtUsuarioMovimiento
            )

        val txtEntrada: TextView =
            itemView.findViewById(
                R.id.txtEntradaMovimiento
            )

        val txtSalida: TextView =
            itemView.findViewById(
                R.id.txtSalidaMovimiento
            )

        val txtEstado: TextView =
            itemView.findViewById(
                R.id.txtEstadoMovimiento
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovimientoViewHolder {

        val vista = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_movimiento,
                parent,
                false
            )

        return MovimientoViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: MovimientoViewHolder,
        position: Int
    ) {

        val movimiento = movimientos[position]

        holder.txtPlaca.text =
            movimiento.placa

        holder.txtUsuario.text =
            movimiento.nombreUsuario

        holder.txtEntrada.text =
            "Entrada: ${movimiento.fechaEntrada}"

        holder.txtSalida.text =
            "Salida: ${movimiento.fechaSalida ?: "Pendiente"}"

        holder.txtEstado.text =
            "Estado: ${movimiento.estado}"

        val colorEstado =
            if (movimiento.estado == "DENTRO") {
                "#C62828"
            } else {
                "#2E7D32"
            }

        holder.txtEstado.setTextColor(
            Color.parseColor(colorEstado)
        )
    }

    override fun getItemCount(): Int {
        return movimientos.size
    }

    fun actualizarLista(
        nuevaLista: List<MovimientoHistorial>
    ) {
        movimientos = nuevaLista
        notifyDataSetChanged()
    }
}