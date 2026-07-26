package pe.edu.idat.idatparking.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import pe.edu.idat.idatparking.R
import pe.edu.idat.idatparking.entity.MovimientoHistorial
import java.util.Locale

class MovimientoAdapter(
    private var movimientos:
    List<MovimientoHistorial>
) : RecyclerView.Adapter<
        MovimientoAdapter.MovimientoViewHolder
        >() {

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

        val cardEstado: MaterialCardView =
            itemView.findViewById(
                R.id.cardEstadoMovimientoItem
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovimientoViewHolder {

        val vista =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_movimiento,
                    parent,
                    false
                )

        return MovimientoViewHolder(
            vista
        )
    }

    override fun onBindViewHolder(
        holder: MovimientoViewHolder,
        position: Int
    ) {
        val movimiento =
            movimientos[position]

        val estado =
            movimiento.estado
                .uppercase(Locale.ROOT)

        holder.txtPlaca.text =
            movimiento.placa
                .uppercase(Locale.ROOT)

        holder.txtUsuario.text =
            movimiento.nombreUsuario

        holder.txtEntrada.text =
            movimiento.fechaEntrada

        holder.txtSalida.text =
            movimiento.fechaSalida
                ?: "Pendiente"

        holder.txtEstado.text =
            estado

        if (estado == "DENTRO") {
            holder.cardEstado
                .setCardBackgroundColor(
                    Color.parseColor(
                        "#FFF3E0"
                    )
                )

            holder.txtEstado.setTextColor(
                Color.parseColor(
                    "#E65100"
                )
            )
        } else {
            holder.cardEstado
                .setCardBackgroundColor(
                    Color.parseColor(
                        "#E8F5E9"
                    )
                )

            holder.txtEstado.setTextColor(
                Color.parseColor(
                    "#2E7D32"
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return movimientos.size
    }

    fun actualizarLista(
        nuevaLista:
        List<MovimientoHistorial>
    ) {
        movimientos = nuevaLista
        notifyDataSetChanged()
    }
}