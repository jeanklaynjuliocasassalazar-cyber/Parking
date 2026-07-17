package pe.edu.idat.idatparking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.entity.VehiculoSeguridad
import pe.edu.idat.idatparking.repository.SeguridadRepository

class SeguridadActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var seguridadRepository: SeguridadRepository

    private lateinit var txtBienvenida: TextView
    private lateinit var edtPlacaBuscar: EditText
    private lateinit var btnBuscar: Button
    private lateinit var txtMensajeBusqueda: TextView

    private lateinit var contenedorResultado: LinearLayout
    private lateinit var txtUsuarioVehiculo: TextView
    private lateinit var txtCorreoVehiculo: TextView
    private lateinit var txtPlacaVehiculo: TextView
    private lateinit var txtDetalleVehiculo: TextView
    private lateinit var txtEstadoSolicitud: TextView
    private lateinit var txtEstadoMovimiento: TextView

    private lateinit var btnRegistrarEntrada: Button
    private lateinit var btnRegistrarSalida: Button
    private lateinit var btnCerrarSesion: Button

    private var vehiculoActual: VehiculoSeguridad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguridad)

        sessionManager = SessionManager(this)
        seguridadRepository = SeguridadRepository(this)

        enlazarControles()
        mostrarDatosUsuario()
        configurarEventos()
    }

    private fun enlazarControles() {
        txtBienvenida =
            findViewById(R.id.txtBienvenidaSeguridad)

        edtPlacaBuscar =
            findViewById(R.id.edtPlacaBuscar)

        btnBuscar =
            findViewById(R.id.btnBuscarVehiculo)

        txtMensajeBusqueda =
            findViewById(R.id.txtMensajeBusqueda)

        contenedorResultado =
            findViewById(R.id.contenedorResultadoVehiculo)

        txtUsuarioVehiculo =
            findViewById(R.id.txtUsuarioVehiculo)

        txtCorreoVehiculo =
            findViewById(R.id.txtCorreoVehiculo)

        txtPlacaVehiculo =
            findViewById(R.id.txtPlacaVehiculo)

        txtDetalleVehiculo =
            findViewById(R.id.txtDetalleVehiculoSeguridad)

        txtEstadoSolicitud =
            findViewById(R.id.txtEstadoSolicitudSeguridad)

        txtEstadoMovimiento =
            findViewById(R.id.txtEstadoMovimientoSeguridad)

        btnRegistrarEntrada =
            findViewById(R.id.btnRegistrarEntrada)

        btnRegistrarSalida =
            findViewById(R.id.btnRegistrarSalida)

        btnCerrarSesion =
            findViewById(R.id.btnCerrarSesionSeguridad)
    }

    private fun mostrarDatosUsuario() {
        txtBienvenida.text =
            "Bienvenido, ${sessionManager.obtenerNombre()}"
    }

    private fun configurarEventos() {

        btnBuscar.setOnClickListener {
            buscarVehiculo()
        }

        btnRegistrarEntrada.setOnClickListener {
            confirmarMovimiento(esEntrada = true)
        }

        btnRegistrarSalida.setOnClickListener {
            confirmarMovimiento(esEntrada = false)
        }

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun buscarVehiculo() {

        val placa =
            edtPlacaBuscar.text.toString().trim()

        if (placa.isEmpty()) {
            edtPlacaBuscar.error =
                "Ingrese una placa."

            edtPlacaBuscar.requestFocus()
            return
        }

        val vehiculo =
            seguridadRepository
                .buscarVehiculoPorPlaca(placa)

        if (vehiculo == null) {
            vehiculoActual = null
            contenedorResultado.visibility = View.GONE

            txtMensajeBusqueda.text =
                "No se encontró un vehículo con esa placa."

            txtMensajeBusqueda.setTextColor(
                Color.parseColor("#C62828")
            )

            txtMensajeBusqueda.visibility =
                View.VISIBLE

            return
        }

        vehiculoActual = vehiculo
        txtMensajeBusqueda.visibility = View.GONE

        mostrarResultado(vehiculo)
    }

    private fun mostrarResultado(
        vehiculo: VehiculoSeguridad
    ) {

        contenedorResultado.visibility = View.VISIBLE

        txtUsuarioVehiculo.text =
            vehiculo.nombreUsuario

        txtCorreoVehiculo.text =
            vehiculo.correoUsuario

        txtPlacaVehiculo.text =
            "Placa: ${vehiculo.placa}"

        txtDetalleVehiculo.text = """
            Marca: ${vehiculo.marca}
            Color: ${vehiculo.color}
            Tipo: ${vehiculo.tipo}
        """.trimIndent()

        txtEstadoSolicitud.text =
            "Solicitud: ${vehiculo.solicitudEstado}"

        val colorSolicitud = when (
            vehiculo.solicitudEstado
        ) {
            "APROBADO" -> "#2E7D32"
            "RECHAZADO" -> "#C62828"
            else -> "#EF6C00"
        }

        txtEstadoSolicitud.setTextColor(
            Color.parseColor(colorSolicitud)
        )

        if (vehiculo.estaDentro) {

            txtEstadoMovimiento.text =
                "Ubicación: DENTRO DEL ESTACIONAMIENTO\n" +
                        "Entrada: ${vehiculo.fechaEntrada ?: "-"}"

            txtEstadoMovimiento.setTextColor(
                Color.parseColor("#C62828")
            )

        } else {

            txtEstadoMovimiento.text =
                "Ubicación: FUERA DEL ESTACIONAMIENTO"

            txtEstadoMovimiento.setTextColor(
                Color.parseColor("#2E7D32")
            )
        }

        actualizarBotones(vehiculo)
    }

    private fun actualizarBotones(
        vehiculo: VehiculoSeguridad
    ) {

        if (
            vehiculo.solicitudEstado != "APROBADO"
        ) {
            btnRegistrarEntrada.visibility = View.GONE
            btnRegistrarSalida.visibility = View.GONE
            return
        }

        if (vehiculo.estaDentro) {
            btnRegistrarEntrada.visibility = View.GONE
            btnRegistrarSalida.visibility = View.VISIBLE
        } else {
            btnRegistrarEntrada.visibility = View.VISIBLE
            btnRegistrarSalida.visibility = View.GONE
        }
    }

    private fun confirmarMovimiento(
        esEntrada: Boolean
    ) {

        val vehiculo = vehiculoActual ?: return

        val accion = if (esEntrada) {
            "entrada"
        } else {
            "salida"
        }

        AlertDialog.Builder(this)
            .setTitle("Registrar $accion")
            .setMessage(
                "¿Confirmas la $accion del vehículo ${vehiculo.placa}?"
            )
            .setPositiveButton("CONFIRMAR") { _, _ ->
                procesarMovimiento(
                    vehiculo = vehiculo,
                    esEntrada = esEntrada
                )
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    private fun procesarMovimiento(
        vehiculo: VehiculoSeguridad,
        esEntrada: Boolean
    ) {

        val resultado = if (esEntrada) {
            seguridadRepository.registrarEntrada(
                vehiculo.vehiculoId
            )
        } else {
            seguridadRepository.registrarSalida(
                vehiculo.vehiculoId
            )
        }

        Toast.makeText(
            this,
            resultado.mensaje,
            Toast.LENGTH_LONG
        ).show()

        if (resultado.exito) {
            buscarVehiculo()
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