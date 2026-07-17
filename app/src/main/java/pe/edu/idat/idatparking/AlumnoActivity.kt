package pe.edu.idat.idatparking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.repository.RegistroRepository

class AlumnoActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var registroRepository: RegistroRepository

    private lateinit var txtBienvenida: TextView
    private lateinit var txtRol: TextView
    private lateinit var txtEstadoSolicitud: TextView
    private lateinit var txtDetalleVehiculo: TextView
    private lateinit var btnRegistrarVehiculo: Button
    private lateinit var btnCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumno)

        sessionManager = SessionManager(this)
        registroRepository = RegistroRepository(this)

        enlazarControles()
        mostrarDatosUsuario()
        configurarEventos()
    }

    override fun onResume() {
        super.onResume()
        cargarEstadoSolicitud()
    }

    private fun enlazarControles() {
        txtBienvenida = findViewById(R.id.txtBienvenidaAlumno)
        txtRol = findViewById(R.id.txtRolAlumno)
        txtEstadoSolicitud = findViewById(R.id.txtEstadoSolicitud)
        txtDetalleVehiculo = findViewById(R.id.txtDetalleVehiculo)
        btnRegistrarVehiculo = findViewById(R.id.btnRegistrarVehiculo)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionAlumno)
    }

    private fun mostrarDatosUsuario() {
        txtBienvenida.text =
            "Bienvenido, ${sessionManager.obtenerNombre()}"

        txtRol.text =
            "Rol: ${sessionManager.obtenerRol()}"
    }

    private fun configurarEventos() {
        btnRegistrarVehiculo.setOnClickListener {
            val intent = Intent(
                this,
                RegistroVehiculoActivity::class.java
            )

            startActivity(intent)
        }

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cargarEstadoSolicitud() {
        val usuarioId = sessionManager.obtenerIdUsuario()

        val solicitud =
            registroRepository.obtenerSolicitudPorUsuario(usuarioId)

        if (solicitud == null) {
            txtEstadoSolicitud.text =
                "Todavía no tienes una solicitud registrada."

            txtEstadoSolicitud.setTextColor(
                Color.parseColor("#555555")
            )

            txtDetalleVehiculo.visibility = View.GONE
            btnRegistrarVehiculo.visibility = View.VISIBLE
            return
        }

        txtEstadoSolicitud.text =
            "Estado de solicitud: ${solicitud.estado}"

        val colorEstado = when (solicitud.estado) {
            "APROBADO" -> "#2E7D32"
            "RECHAZADO" -> "#C62828"
            else -> "#EF6C00"
        }

        txtEstadoSolicitud.setTextColor(
            Color.parseColor(colorEstado)
        )

        txtDetalleVehiculo.text = """
            Placa: ${solicitud.placa}
            Marca: ${solicitud.marca}
            Color: ${solicitud.color}
            Tipo: ${solicitud.tipo}
            Fecha: ${solicitud.fechaSolicitud}
        """.trimIndent()

        txtDetalleVehiculo.visibility = View.VISIBLE
        btnRegistrarVehiculo.visibility = View.GONE
    }

    private fun cerrarSesion() {
        sessionManager.cerrarSesion()

        val intent = Intent(
            this,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }
}