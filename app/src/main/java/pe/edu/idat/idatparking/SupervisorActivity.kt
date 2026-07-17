package pe.edu.idat.idatparking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pe.edu.idat.idatparking.adapter.SolicitudAdapter
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.entity.SolicitudSupervisor
import pe.edu.idat.idatparking.repository.SolicitudRepository

class SupervisorActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var solicitudRepository: SolicitudRepository
    private lateinit var solicitudAdapter: SolicitudAdapter

    private lateinit var txtBienvenida: TextView
    private lateinit var txtSinSolicitudes: TextView
    private lateinit var rvSolicitudes: RecyclerView
    private lateinit var btnActualizar: Button
    private lateinit var btnCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor)

        sessionManager = SessionManager(this)
        solicitudRepository = SolicitudRepository(this)

        enlazarControles()
        configurarRecyclerView()
        configurarEventos()
    }

    override fun onResume() {
        super.onResume()
        cargarSolicitudes()
    }

    private fun enlazarControles() {
        txtBienvenida =
            findViewById(R.id.txtBienvenidaSupervisor)

        txtSinSolicitudes =
            findViewById(R.id.txtSinSolicitudes)

        rvSolicitudes =
            findViewById(R.id.rvSolicitudes)

        btnActualizar =
            findViewById(R.id.btnActualizarSolicitudes)

        btnCerrarSesion =
            findViewById(R.id.btnCerrarSesionSupervisor)

        txtBienvenida.text =
            "Bienvenido, ${sessionManager.obtenerNombre()}"
    }

    private fun configurarRecyclerView() {

        solicitudAdapter = SolicitudAdapter(
            solicitudes = emptyList(),
            onAprobar = { solicitud ->
                confirmarCambioEstado(
                    solicitud,
                    "APROBADO"
                )
            },
            onRechazar = { solicitud ->
                confirmarCambioEstado(
                    solicitud,
                    "RECHAZADO"
                )
            }
        )

        rvSolicitudes.layoutManager =
            LinearLayoutManager(this)

        rvSolicitudes.adapter =
            solicitudAdapter
    }

    private fun configurarEventos() {

        btnActualizar.setOnClickListener {
            cargarSolicitudes()

            Toast.makeText(
                this,
                "Lista actualizada.",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cargarSolicitudes() {

        val solicitudes =
            solicitudRepository
                .listarSolicitudesPendientes()

        solicitudAdapter.actualizarLista(
            solicitudes
        )

        if (solicitudes.isEmpty()) {
            txtSinSolicitudes.visibility = View.VISIBLE
            rvSolicitudes.visibility = View.GONE
        } else {
            txtSinSolicitudes.visibility = View.GONE
            rvSolicitudes.visibility = View.VISIBLE
        }
    }

    private fun confirmarCambioEstado(
        solicitud: SolicitudSupervisor,
        nuevoEstado: String
    ) {

        val accion = if (
            nuevoEstado == "APROBADO"
        ) {
            "aprobar"
        } else {
            "rechazar"
        }

        AlertDialog.Builder(this)
            .setTitle(
                "${accion.replaceFirstChar { it.uppercase() }} solicitud"
            )
            .setMessage(
                "¿Deseas $accion la solicitud del vehículo ${solicitud.placa}?"
            )
            .setPositiveButton("SÍ") { _, _ ->
                cambiarEstado(
                    solicitud,
                    nuevoEstado
                )
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    private fun cambiarEstado(
        solicitud: SolicitudSupervisor,
        nuevoEstado: String
    ) {

        val actualizado =
            solicitudRepository.actualizarEstado(
                solicitudId = solicitud.solicitudId,
                nuevoEstado = nuevoEstado
            )

        if (actualizado) {

            Toast.makeText(
                this,
                "Solicitud ${nuevoEstado.lowercase()} correctamente.",
                Toast.LENGTH_LONG
            ).show()

            cargarSolicitudes()

        } else {

            Toast.makeText(
                this,
                "No se pudo actualizar la solicitud.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun cerrarSesion() {

        sessionManager.cerrarSesion()

        val intent = Intent(
            this,
            MainActivity::class.java
        ).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }
}