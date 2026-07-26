package pe.edu.idat.idatparking.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import pe.edu.idat.idatparking.R
import pe.edu.idat.idatparking.RegistroVehiculoActivity
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.repository.RegistroRepository

class MiVehiculoFragment :
    Fragment(R.layout.fragment_mi_vehiculo) {

    private lateinit var sessionManager: SessionManager
    private lateinit var registroRepository: RegistroRepository

    private lateinit var cardSinVehiculo: MaterialCardView
    private lateinit var cardVehiculoRegistrado: MaterialCardView
    private lateinit var cardEstadoSolicitud: MaterialCardView

    private lateinit var txtEstado: TextView
    private lateinit var txtMensajeEstado: TextView
    private lateinit var txtPlaca: TextView
    private lateinit var txtMarca: TextView
    private lateinit var txtColor: TextView
    private lateinit var txtTipo: TextView
    private lateinit var txtFecha: TextView

    private lateinit var btnRegistrar: MaterialButton

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

        enlazarControles(view)
        configurarEventos()
    }

    private fun enlazarControles(view: View) {
        cardSinVehiculo =
            view.findViewById(
                R.id.cardSinVehiculo
            )

        cardVehiculoRegistrado =
            view.findViewById(
                R.id.cardVehiculoRegistrado
            )

        cardEstadoSolicitud =
            view.findViewById(
                R.id.cardEstadoSolicitud
            )

        txtEstado =
            view.findViewById(
                R.id.txtEstadoMiVehiculo
            )

        txtMensajeEstado =
            view.findViewById(
                R.id.txtMensajeEstadoMiVehiculo
            )

        txtPlaca =
            view.findViewById(
                R.id.txtPlacaMiVehiculo
            )

        txtMarca =
            view.findViewById(
                R.id.txtMarcaMiVehiculo
            )

        txtColor =
            view.findViewById(
                R.id.txtColorMiVehiculo
            )

        txtTipo =
            view.findViewById(
                R.id.txtTipoMiVehiculo
            )

        txtFecha =
            view.findViewById(
                R.id.txtFechaMiVehiculo
            )

        btnRegistrar =
            view.findViewById(
                R.id.btnRegistrarMiVehiculo
            )
    }

    private fun configurarEventos() {
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
            mostrarEstadoSinVehiculo()
            return
        }

        mostrarVehiculoRegistrado()

        txtPlaca.text =
            solicitud.placa

        txtMarca.text =
            solicitud.marca

        txtColor.text =
            solicitud.color

        txtTipo.text =
            solicitud.tipo

        txtFecha.text =
            "Solicitud registrada: ${solicitud.fechaSolicitud}"

        configurarEstadoSolicitud(
            solicitud.estado
        )
    }

    private fun mostrarEstadoSinVehiculo() {
        cardSinVehiculo.visibility =
            View.VISIBLE

        cardVehiculoRegistrado.visibility =
            View.GONE
    }

    private fun mostrarVehiculoRegistrado() {
        cardSinVehiculo.visibility =
            View.GONE

        cardVehiculoRegistrado.visibility =
            View.VISIBLE
    }

    private fun configurarEstadoSolicitud(
        estado: String
    ) {
        val estadoNormalizado =
            estado.uppercase()

        txtEstado.text =
            estadoNormalizado

        when (estadoNormalizado) {
            "APROBADO" -> {
                aplicarEstiloEstado(
                    colorFondo = "#E8F5E9",
                    colorTexto = "#2E7D32",
                    mensaje = "Tu vehículo está autorizado para ingresar al estacionamiento."
                )
            }

            "RECHAZADO" -> {
                aplicarEstiloEstado(
                    colorFondo = "#FFEBEE",
                    colorTexto = "#C62828",
                    mensaje = "La solicitud fue rechazada por el supervisor."
                )
            }

            else -> {
                aplicarEstiloEstado(
                    colorFondo = "#FFF3E0",
                    colorTexto = "#EF6C00",
                    mensaje = "Tu solicitud está pendiente de revisión."
                )
            }
        }
    }

    private fun aplicarEstiloEstado(
        colorFondo: String,
        colorTexto: String,
        mensaje: String
    ) {
        cardEstadoSolicitud.setCardBackgroundColor(
            Color.parseColor(colorFondo)
        )

        txtEstado.setTextColor(
            Color.parseColor(colorTexto)
        )

        txtMensajeEstado.text =
            mensaje
    }
}