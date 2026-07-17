package pe.edu.idat.idatparking.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import pe.edu.idat.idatparking.R
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.repository.MovimientoRepository

class InicioFragment :
    Fragment(R.layout.fragment_inicio) {

    private lateinit var sessionManager:
            SessionManager

    private lateinit var movimientoRepository:
            MovimientoRepository

    private lateinit var txtBienvenida: TextView
    private lateinit var txtRol: TextView
    private lateinit var txtCapacidad: TextView
    private lateinit var txtOcupados: TextView
    private lateinit var txtDisponibles: TextView

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

        txtBienvenida =
            view.findViewById(
                R.id.txtBienvenidaInicio
            )

        txtRol =
            view.findViewById(
                R.id.txtRolInicio
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

        txtBienvenida.text =
            "Bienvenido, ${sessionManager.obtenerNombre()}"

        txtRol.text =
            "Rol: ${sessionManager.obtenerRol()}"
    }

    override fun onResume() {
        super.onResume()
        cargarDisponibilidad()
    }

    private fun cargarDisponibilidad() {

        val estadistica =
            movimientoRepository.obtenerEstadistica()

        txtCapacidad.text =
            estadistica.capacidadTotal.toString()

        txtOcupados.text =
            estadistica.ocupados.toString()

        txtDisponibles.text =
            estadistica.disponibles.toString()
    }
}