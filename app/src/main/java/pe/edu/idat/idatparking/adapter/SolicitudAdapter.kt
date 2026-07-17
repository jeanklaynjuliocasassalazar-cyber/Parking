package pe.edu.idat.idatparking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pe.edu.idat.idatparking.R
import pe.edu.idat.idatparking.entity.SolicitudSupervisor

class SolicitudAdapter(
    private var solicitudes: List<SolicitudSupervisor>,
    private val onAprobar: (SolicitudSupervisor) -> Unit,
    private val onRechazar: (SolicitudSupervisor) -> Unit
) : RecyclerView.Adapter<SolicitudAdapter.SolicitudViewHolder>() {

    class SolicitudViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtNombre: TextView =
            itemView.findViewById(R.id.txtNombreSolicitud)

        val txtCorreo: TextView =
            itemView.findViewById(R.id.txtCorreoSolicitud)

        val txtPlaca: TextView =
            itemView.findViewById(R.id.txtPlacaSolicitud)

        val txtDetalles: TextView =
            itemView.findViewById(R.id.txtDetallesSolicitud)

        val txtFecha: TextView =
            itemView.findViewById(R.id.txtFechaSolicitud)

        val txtEstado: TextView =
            itemView.findViewById(R.id.txtEstadoItemSolicitud)

        val btnAprobar: Button =
            itemView.findViewById(R.id.btnAprobarSolicitud)

        val btnRechazar: Button =
            itemView.findViewById(R.id.btnRechazarSolicitud)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SolicitudViewHolder {

        val vista = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_solicitud,
                parent,
                false
            )

        return SolicitudViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: SolicitudViewHolder,
        position: Int
    ) {

        val solicitud = solicitudes[position]

        holder.txtNombre.text =
            solicitud.nombreUsuario

        holder.txtCorreo.text =
            solicitud.correoUsuario

        holder.txtPlaca.text =
            "Placa: ${solicitud.placa}"

        holder.txtDetalles.text = """
            Marca: ${solicitud.marca}
            Color: ${solicitud.color}
            Tipo: ${solicitud.tipo}
        """.trimIndent()

        holder.txtFecha.text =
            "Fecha: ${solicitud.fechaSolicitud}"

        holder.txtEstado.text =
            "Estado: ${solicitud.estado}"

        holder.btnAprobar.setOnClickListener {
            onAprobar(solicitud)
        }

        holder.btnRechazar.setOnClickListener {
            onRechazar(solicitud)
        }
    }

    override fun getItemCount(): Int {
        return solicitudes.size
    }

    fun actualizarLista(
        nuevaLista: List<SolicitudSupervisor>
    ) {
        solicitudes = nuevaLista
        notifyDataSetChanged()
    }
}