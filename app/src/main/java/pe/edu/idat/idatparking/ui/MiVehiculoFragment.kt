package pe.edu.idat.idatparking.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import pe.edu.idat.idatparking.R
import pe.edu.idat.idatparking.RegistroVehiculoActivity
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.repository.RegistroRepository

class MiVehiculoFragment :
    Fragment(R.layout.fragment_mi_vehiculo) {

    private lateinit var sessionManager:
            SessionManager

    private lateinit var registroRepository:
            RegistroRepository

    private lateinit var txtEstado: TextView
    private lateinit var txtDetalle: TextView
    private lateinit var btnRegistrar: Button

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        sessionManager =
            SessionManager(requireContext())

        registroRepository =
            RegistroRepository(requireContext())

        txtEstado =
            view.findViewById(
                R.id.txtEstadoMiVehiculo
            )

        txtDetalle =
            view.findViewById(
                R.id.txtDetalleMiVehiculo
            )

        btnRegistrar =
            view.findViewById(
                R.id.btnRegistrarMiVehiculo
            )

        btnRegistrar.setOnClickListener {

            val intent = Intent(
                requireContext(),
                RegistroVehiculoActivity::class.java
            )

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarVehiculo()
    }

    private fun cargarVehiculo() {

        val usuarioId =
            sessionManager.obtenerIdUsuario()

        val solicitud =
            registroRepository
                .obtenerSolicitudPorUsuario(
                    usuarioId
                )

        if (solicitud == null) {

            txtEstado.text =
                "No tienes un vehículo registrado."

            txtEstado.setTextColor(
                Color.parseColor("#555555")
            )

            txtDetalle.visibility =
                View.GONE

            btnRegistrar.visibility =
                View.VISIBLE

            return
        }

        txtEstado.text =
            "Estado: ${solicitud.estado}"

        val color = when (solicitud.estado) {
            "APROBADO" -> "#2E7D32"
            "RECHAZADO" -> "#C62828"
            else -> "#EF6C00"
        }

        txtEstado.setTextColor(
            Color.parseColor(color)
        )

        txtDetalle.text = """
            Placa: ${solicitud.placa}
            Marca: ${solicitud.marca}
            Color: ${solicitud.color}
            Tipo: ${solicitud.tipo}
            Fecha de solicitud: ${solicitud.fechaSolicitud}
        """.trimIndent()

        txtDetalle.visibility =
            View.VISIBLE

        btnRegistrar.visibility =
            View.GONE
    }
}