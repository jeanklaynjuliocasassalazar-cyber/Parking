package pe.edu.idat.idatparking.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.LinearProgressIndicator
import pe.edu.idat.idatparking.R
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.repository.MovimientoRepository

class InicioFragment :
    Fragment(R.layout.fragment_inicio) {

    private lateinit var sessionManager: SessionManager
    private lateinit var movimientoRepository: MovimientoRepository

    private lateinit var txtBienvenida: TextView
    private lateinit var txtCapacidad: TextView
    private lateinit var txtOcupados: TextView
    private lateinit var txtDisponibles: TextView
    private lateinit var txtResumenDisponibilidad: TextView

    private lateinit var progressOcupacion:
            LinearProgressIndicator

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

        movimientoRepository =
            MovimientoRepository(requireContext())

        enlazarControles(view)
        mostrarBienvenida()
    }

    private fun enlazarControles(view: View) {
        txtBienvenida =
            view.findViewById(
                R.id.txtBienvenidaInicio
            )

        txtCapacidad =
            view.findViewById(
                R.id.txtCapacidadInicio
            )

        txtOcupados =
            view.findViewById(
                R.id.txtOcupadosInicio
            )

        txtDisponibles =
            view.findViewById(
                R.id.txtDisponiblesInicio
            )

        txtResumenDisponibilidad =
            view.findViewById(
                R.id.txtResumenDisponibilidad
            )

        progressOcupacion =
            view.findViewById(
                R.id.progressOcupacionInicio
            )
    }

    private fun mostrarBienvenida() {
        val nombre =
            sessionManager
                .obtenerNombre()
                .ifBlank {
                    "usuario"
                }

        txtBienvenida.text =
            "Bienvenido, $nombre"
    }

    override fun onResume() {
        super.onResume()
        cargarDisponibilidad()
    }

    private fun cargarDisponibilidad() {
        val estadistica =
            movimientoRepository
                .obtenerEstadistica()

        val capacidad =
            estadistica.capacidadTotal

        val ocupados =
            estadistica.ocupados

        val disponibles =
            estadistica.disponibles

        txtCapacidad.text =
            "$capacidad espacios"

        txtOcupados.text =
            ocupados.toString()

        txtDisponibles.text =
            disponibles.toString()

        progressOcupacion.max =
            capacidad.coerceAtLeast(1)

        progressOcupacion.setProgressCompat(
            ocupados,
            true
        )

        txtResumenDisponibilidad.text =
            obtenerMensajeDisponibilidad(
                capacidad = capacidad,
                ocupados = ocupados,
                disponibles = disponibles
            )
    }

    private fun obtenerMensajeDisponibilidad(
        capacidad: Int,
        ocupados: Int,
        disponibles: Int
    ): String {
        return when {
            capacidad <= 0 -> {
                "No se ha definido la capacidad del estacionamiento."
            }

            disponibles <= 0 -> {
                "El estacionamiento se encuentra lleno."
            }

            ocupados == 0 -> {
                "Todos los espacios están disponibles."
            }

            disponibles <= 5 -> {
                "Quedan pocos espacios disponibles: $disponibles de $capacidad."
            }

            else -> {
                "Hay $disponibles espacios disponibles de un total de $capacidad."
            }
        }
    }
}