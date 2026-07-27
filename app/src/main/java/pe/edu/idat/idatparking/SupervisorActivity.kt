package pe.edu.idat.idatparking

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pe.edu.idat.idatparking.adapter.SolicitudAdapter
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.entity.SolicitudSupervisor
import pe.edu.idat.idatparking.entity.Usuario
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

        if (
            !sessionManager.tieneRolPermitido(
                Usuario.ROL_SUPERVISOR
            )
        ) {
            sessionManager.cerrarSesion()
            regresarAlLogin()
            return
        }

        solicitudRepository =
            SolicitudRepository(this)

        enlazarControles()
        configurarRecyclerView()
        configurarEventos()
    }

    override fun onResume() {
        super.onResume()

        if (
            !sessionManager.tieneRolPermitido(
                Usuario.ROL_SUPERVISOR
            )
        ) {
            sessionManager.cerrarSesion()
            regresarAlLogin()
            return
        }

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
                    confirmarAprobacion(
                        solicitud
                    )
                },
                onRechazar = { solicitud ->
                    solicitarMotivoRechazo(
                        solicitud
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

    private fun confirmarAprobacion(
        solicitud: SolicitudSupervisor
    ) {
        AlertDialog.Builder(this)
            .setTitle(
                "Aprobar solicitud"
            )
            .setMessage(
                "¿Deseas aprobar la solicitud del vehículo ${solicitud.placa}?"
            )
            .setPositiveButton(
                "APROBAR"
            ) { _, _ ->
                cambiarEstado(
                    solicitud = solicitud,
                    nuevoEstado = "APROBADO",
                    observacion = null
                )
            }
            .setNegativeButton(
                "CANCELAR",
                null
            )
            .show()
    }

    private fun solicitarMotivoRechazo(
        solicitud: SolicitudSupervisor
    ) {
        val contenedor =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    convertirDp(24),
                    convertirDp(10),
                    convertirDp(24),
                    0
                )
            }

        val tilObservacion =
            TextInputLayout(this).apply {
                hint =
                    "Motivo del rechazo"

                helperText =
                    "Indica qué información debe corregir el usuario."
            }

        val edtObservacion =
            TextInputEditText(this).apply {
                inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

                minLines = 3
                maxLines = 5
                gravity =
                    android.view.Gravity.TOP
            }

        tilObservacion.addView(
            edtObservacion,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        contenedor.addView(
            tilObservacion
        )

        val dialogo =
            AlertDialog.Builder(this)
                .setTitle(
                    "Rechazar solicitud"
                )
                .setMessage(
                    "Vehículo: ${solicitud.placa}"
                )
                .setView(
                    contenedor
                )
                .setPositiveButton(
                    "RECHAZAR",
                    null
                )
                .setNegativeButton(
                    "CANCELAR",
                    null
                )
                .create()

        dialogo.setOnShowListener {
            val btnRechazar =
                dialogo.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            btnRechazar.setOnClickListener {
                val observacion =
                    edtObservacion.text
                        ?.toString()
                        .orEmpty()
                        .trim()

                if (observacion.isEmpty()) {
                    tilObservacion.error =
                        "Ingrese el motivo del rechazo."

                    edtObservacion.requestFocus()
                    return@setOnClickListener
                }

                tilObservacion.error =
                    null

                val actualizado =
                    cambiarEstado(
                        solicitud = solicitud,
                        nuevoEstado = "RECHAZADO",
                        observacion = observacion
                    )

                if (actualizado) {
                    dialogo.dismiss()
                }
            }
        }

        dialogo.show()
    }

    private fun cambiarEstado(
        solicitud: SolicitudSupervisor,
        nuevoEstado: String,
        observacion: String?
    ): Boolean {
        val actualizado =
            solicitudRepository
                .actualizarEstado(
                    solicitudId =
                        solicitud.solicitudId,
                    nuevoEstado =
                        nuevoEstado,
                    observacion =
                        observacion
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

        return actualizado
    }

    private fun confirmarCierreSesion() {
        AlertDialog.Builder(this)
            .setTitle(
                "Cerrar sesión"
            )
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

        startActivity(
            intent
        )

        finish()
    }

    private fun convertirDp(
        valor: Int
    ): Int {
        return (
                valor *
                        resources.displayMetrics.density
                ).toInt()
    }
}