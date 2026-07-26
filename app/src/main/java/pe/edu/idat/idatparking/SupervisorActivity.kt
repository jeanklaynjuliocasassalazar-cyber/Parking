package pe.edu.idat.idatparking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import pe.edu.idat.idatparking.adapter.SolicitudAdapter
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.entity.SolicitudSupervisor
import pe.edu.idat.idatparking.repository.SolicitudRepository

class SupervisorActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var solicitudRepository: SolicitudRepository
    private lateinit var solicitudAdapter: SolicitudAdapter

    private lateinit var txtBienvenida: TextView
    private lateinit var txtCantidadSolicitudes: TextView
    private lateinit var txtSinSolicitudes: TextView

    private lateinit var cardSinSolicitudes: MaterialCardView
    private lateinit var rvSolicitudes: RecyclerView

    private lateinit var btnActualizar: MaterialButton
    private lateinit var btnCerrarSesion: MaterialButton

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor)

        sessionManager =
            SessionManager(this)

        solicitudRepository =
            SolicitudRepository(this)

        if (!sessionManager.existeSesion()) {
            regresarAlLogin()
            return
        }

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
            findViewById(
                R.id.txtBienvenidaSupervisor
            )

        txtCantidadSolicitudes =
            findViewById(
                R.id.txtCantidadSolicitudes
            )

        txtSinSolicitudes =
            findViewById(
                R.id.txtSinSolicitudes
            )

        cardSinSolicitudes =
            findViewById(
                R.id.cardSinSolicitudesSupervisor
            )

        rvSolicitudes =
            findViewById(
                R.id.rvSolicitudes
            )

        btnActualizar =
            findViewById(
                R.id.btnActualizarSolicitudes
            )

        btnCerrarSesion =
            findViewById(
                R.id.btnCerrarSesionSupervisor
            )

        val nombre =
            sessionManager
                .obtenerNombre()
                .ifBlank {
                    "Supervisor"
                }

        txtBienvenida.text =
            "Bienvenido, $nombre"
    }

    private fun configurarRecyclerView() {
        solicitudAdapter =
            SolicitudAdapter(
                solicitudes = emptyList(),
                onAprobar = { solicitud ->
                    confirmarCambioEstado(
                        solicitud = solicitud,
                        nuevoEstado = "APROBADO"
                    )
                },
                onRechazar = { solicitud ->
                    confirmarCambioEstado(
                        solicitud = solicitud,
                        nuevoEstado = "RECHAZADO"
                    )
                }
            )

        rvSolicitudes.layoutManager =
            LinearLayoutManager(this)

        rvSolicitudes.adapter =
            solicitudAdapter

        rvSolicitudes.setHasFixedSize(
            true
        )
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
            confirmarCierreSesion()
        }
    }

    private fun cargarSolicitudes() {
        val solicitudes =
            solicitudRepository
                .listarSolicitudesPendientes()

        solicitudAdapter.actualizarLista(
            solicitudes
        )

        txtCantidadSolicitudes.text =
            solicitudes.size.toString()

        if (solicitudes.isEmpty()) {
            cardSinSolicitudes.visibility =
                View.VISIBLE

            rvSolicitudes.visibility =
                View.GONE
        } else {
            cardSinSolicitudes.visibility =
                View.GONE

            rvSolicitudes.visibility =
                View.VISIBLE
        }
    }

    private fun confirmarCambioEstado(
        solicitud: SolicitudSupervisor,
        nuevoEstado: String
    ) {
        val esAprobacion =
            nuevoEstado == "APROBADO"

        val accion =
            if (esAprobacion) {
                "aprobar"
            } else {
                "rechazar"
            }

        val titulo =
            if (esAprobacion) {
                "Aprobar solicitud"
            } else {
                "Rechazar solicitud"
            }

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(
                "¿Deseas $accion la solicitud del vehículo ${solicitud.placa}?"
            )
            .setPositiveButton(
                if (esAprobacion) {
                    "APROBAR"
                } else {
                    "RECHAZAR"
                }
            ) { _, _ ->
                cambiarEstado(
                    solicitud = solicitud,
                    nuevoEstado = nuevoEstado
                )
            }
            .setNegativeButton(
                "CANCELAR",
                null
            )
            .show()
    }

    private fun cambiarEstado(
        solicitud: SolicitudSupervisor,
        nuevoEstado: String
    ) {
        val actualizado =
            solicitudRepository
                .actualizarEstado(
                    solicitudId =
                        solicitud.solicitudId,
                    nuevoEstado =
                        nuevoEstado
                )

        if (actualizado) {
            val mensaje =
                if (
                    nuevoEstado == "APROBADO"
                ) {
                    "Solicitud aprobada correctamente."
                } else {
                    "Solicitud rechazada correctamente."
                }

            Toast.makeText(
                this,
                mensaje,
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

    private fun confirmarCierreSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage(
                "¿Está seguro de que desea salir de IDAT Parking?"
            )
            .setPositiveButton(
                "SÍ"
            ) { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton(
                "CANCELAR",
                null
            )
            .show()
    }

    private fun cerrarSesion() {
        sessionManager.cerrarSesion()
        regresarAlLogin()
    }

    private fun regresarAlLogin() {
        val intent =
            Intent(
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