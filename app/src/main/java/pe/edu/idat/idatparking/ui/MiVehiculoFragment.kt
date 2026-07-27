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
import pe.edu.idat.idatparking.entity.SolicitudDetalle
import pe.edu.idat.idatparking.repository.RegistroRepository
import java.util.Locale

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
    private lateinit var btnEditarReenviar: MaterialButton

    private var solicitudActual: SolicitudDetalle? = null

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

    private fun enlazarControles(
        view: View
    ) {
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

        btnEditarReenviar =
            view.findViewById(
                R.id.btnEditarReenviarSolicitud
            )
    }

    private fun configurarEventos() {
        btnRegistrar.setOnClickListener {
            abrirRegistroNuevo()
        }

        btnEditarReenviar.setOnClickListener {
            abrirCorreccionSolicitud()
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

        solicitudActual =
            solicitud

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
            solicitud
        )
    }

    private fun mostrarEstadoSinVehiculo() {
        solicitudActual =
            null

        cardSinVehiculo.visibility =
            View.VISIBLE

        cardVehiculoRegistrado.visibility =
            View.GONE

        btnEditarReenviar.visibility =
            View.GONE
    }

    private fun mostrarVehiculoRegistrado() {
        cardSinVehiculo.visibility =
            View.GONE

        cardVehiculoRegistrado.visibility =
            View.VISIBLE
    }

    private fun configurarEstadoSolicitud(
        solicitud: SolicitudDetalle
    ) {
        val estadoNormalizado =
            solicitud.estado
                .uppercase(
                    Locale.ROOT
                )

        txtEstado.text =
            estadoNormalizado

        when (estadoNormalizado) {
            "APROBADO" -> {
                aplicarEstiloEstado(
                    colorFondo = "#E8F5E9",
                    colorTexto = "#2E7D32",
                    mensaje = "Tu vehículo está autorizado para ingresar al estacionamiento."
                )

                btnEditarReenviar.visibility =
                    View.GONE
            }

            "RECHAZADO" -> {
                val observacion =
                    solicitud.observacion
                        ?.trim()
                        .orEmpty()

                val mensaje =
                    if (observacion.isEmpty()) {
                        "La solicitud fue rechazada por el supervisor. Corrige los datos y vuelve a enviarla."
                    } else {
                        """
                        La solicitud fue rechazada por el supervisor.

                        Motivo:
                        $observacion
                        """.trimIndent()
                    }

                aplicarEstiloEstado(
                    colorFondo = "#FFEBEE",
                    colorTexto = "#C62828",
                    mensaje = mensaje
                )

                btnEditarReenviar.visibility =
                    View.VISIBLE
            }

            else -> {
                aplicarEstiloEstado(
                    colorFondo = "#FFF3E0",
                    colorTexto = "#EF6C00",
                    mensaje = "Tu solicitud está pendiente de revisión."
                )

                btnEditarReenviar.visibility =
                    View.GONE
            }
        }
    }

    private fun aplicarEstiloEstado(
        colorFondo: String,
        colorTexto: String,
        mensaje: String
    ) {
        cardEstadoSolicitud
            .setCardBackgroundColor(
                Color.parseColor(
                    colorFondo
                )
            )

        txtEstado.setTextColor(
            Color.parseColor(
                colorTexto
            )
        )

        txtMensajeEstado.text =
            mensaje
    }

    private fun abrirRegistroNuevo() {
        val intent =
            Intent(
                requireContext(),
                RegistroVehiculoActivity::class.java
            )

        startActivity(
            intent
        )
    }

    private fun abrirCorreccionSolicitud() {
        val solicitud =
            solicitudActual ?: return

        if (
            solicitud.estado
                .uppercase(
                    Locale.ROOT
                ) != "RECHAZADO"
        ) {
            return
        }

        val intent =
            Intent(
                requireContext(),
                RegistroVehiculoActivity::class.java
            ).apply {
                putExtra(
                    RegistroVehiculoActivity.EXTRA_MODO_EDICION,
                    true
                )

                putExtra(
                    RegistroVehiculoActivity.EXTRA_VEHICULO_ID,
                    solicitud.vehiculoId
                )

                putExtra(
                    RegistroVehiculoActivity.EXTRA_SOLICITUD_ID,
                    solicitud.solicitudId
                )

                putExtra(
                    RegistroVehiculoActivity.EXTRA_PLACA,
                    solicitud.placa
                )

                putExtra(
                    RegistroVehiculoActivity.EXTRA_MARCA,
                    solicitud.marca
                )

                putExtra(
                    RegistroVehiculoActivity.EXTRA_COLOR,
                    solicitud.color
                )

                putExtra(
                    RegistroVehiculoActivity.EXTRA_TIPO,
                    solicitud.tipo
                )
            }

        startActivity(
            intent
        )
    }
}