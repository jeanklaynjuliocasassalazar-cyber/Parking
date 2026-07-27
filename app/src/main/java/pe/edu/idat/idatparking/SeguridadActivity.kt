package pe.edu.idat.idatparking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.entity.Usuario
import pe.edu.idat.idatparking.entity.VehiculoSeguridad
import pe.edu.idat.idatparking.repository.SeguridadRepository
import java.util.Locale

class SeguridadActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var seguridadRepository: SeguridadRepository

    private lateinit var txtBienvenida: TextView
    private lateinit var edtPlacaBuscar: EditText
    private lateinit var spnPlacasRegistradas: Spinner
    private lateinit var btnBuscar: MaterialButton
    private lateinit var txtMensajeBusqueda: TextView

    private lateinit var contenedorResultado:
            MaterialCardView

    private lateinit var cardEstadoSolicitud:
            MaterialCardView

    private lateinit var cardEstadoMovimiento:
            MaterialCardView

    private lateinit var txtUsuarioVehiculo: TextView
    private lateinit var txtCorreoVehiculo: TextView
    private lateinit var txtPlacaVehiculo: TextView
    private lateinit var txtDetalleVehiculo: TextView
    private lateinit var txtEstadoSolicitud: TextView
    private lateinit var txtEstadoMovimiento: TextView

    private lateinit var btnRegistrarEntrada:
            MaterialButton

    private lateinit var btnRegistrarSalida:
            MaterialButton

    private lateinit var btnCerrarSesion:
            MaterialButton

    private lateinit var btnVerHistorial:
            MaterialButton

    private var vehiculoActual:
            VehiculoSeguridad? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguridad)

        sessionManager =
            SessionManager(this)

        if (!validarAcceso()) {
            return
        }

        seguridadRepository =
            SeguridadRepository(this)

        enlazarControles()
        mostrarDatosUsuario()
        configurarComboPlacas()
        configurarEventos()
        cargarPlacasRegistradas()
    }

    override fun onResume() {
        super.onResume()

        if (!validarAcceso()) {
            return
        }

        if (
            ::spnPlacasRegistradas.isInitialized
        ) {
            cargarPlacasRegistradas()
        }
    }

    private fun validarAcceso(): Boolean {
        if (
            sessionManager.tieneRolPermitido(
                Usuario.ROL_SEGURIDAD
            )
        ) {
            return true
        }

        if (!isFinishing) {
            sessionManager.cerrarSesion()
            regresarAlLogin()
        }

        return false
    }

    private fun enlazarControles() {
        txtBienvenida =
            findViewById(
                R.id.txtBienvenidaSeguridad
            )

        edtPlacaBuscar =
            findViewById(
                R.id.edtPlacaBuscar
            )

        spnPlacasRegistradas =
            findViewById(
                R.id.spnPlacasRegistradas
            )

        btnBuscar =
            findViewById(
                R.id.btnBuscarVehiculo
            )

        txtMensajeBusqueda =
            findViewById(
                R.id.txtMensajeBusqueda
            )

        contenedorResultado =
            findViewById(
                R.id.contenedorResultadoVehiculo
            )

        cardEstadoSolicitud =
            findViewById(
                R.id.cardEstadoSolicitudSeguridad
            )

        cardEstadoMovimiento =
            findViewById(
                R.id.cardEstadoMovimientoSeguridad
            )

        txtUsuarioVehiculo =
            findViewById(
                R.id.txtUsuarioVehiculo
            )

        txtCorreoVehiculo =
            findViewById(
                R.id.txtCorreoVehiculo
            )

        txtPlacaVehiculo =
            findViewById(
                R.id.txtPlacaVehiculo
            )

        txtDetalleVehiculo =
            findViewById(
                R.id.txtDetalleVehiculoSeguridad
            )

        txtEstadoSolicitud =
            findViewById(
                R.id.txtEstadoSolicitudSeguridad
            )

        txtEstadoMovimiento =
            findViewById(
                R.id.txtEstadoMovimientoSeguridad
            )

        btnRegistrarEntrada =
            findViewById(
                R.id.btnRegistrarEntrada
            )

        btnRegistrarSalida =
            findViewById(
                R.id.btnRegistrarSalida
            )

        btnCerrarSesion =
            findViewById(
                R.id.btnCerrarSesionSeguridad
            )

        btnVerHistorial =
            findViewById(
                R.id.btnVerHistorial
            )
    }

    private fun mostrarDatosUsuario() {
        val nombre =
            sessionManager
                .obtenerNombre()
                .ifBlank {
                    "Seguridad"
                }

        txtBienvenida.text =
            "Bienvenido, $nombre"
    }

    private fun configurarComboPlacas() {
        spnPlacasRegistradas
            .onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position <= 0) {
                        return
                    }

                    val placa =
                        parent
                            ?.getItemAtPosition(
                                position
                            )
                            ?.toString()
                            .orEmpty()

                    if (
                        placa.isBlank() ||
                        placa == SIN_PLACAS
                    ) {
                        return
                    }

                    edtPlacaBuscar.setText(
                        placa
                    )

                    edtPlacaBuscar.setSelection(
                        placa.length
                    )

                    edtPlacaBuscar.error = null

                    ocultarResultadoActual()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun cargarPlacasRegistradas() {
        val placas =
            seguridadRepository
                .listarPlacasRegistradas()

        val opciones =
            if (placas.isEmpty()) {
                listOf(
                    SIN_PLACAS
                )
            } else {
                mutableListOf(
                    SELECCIONE_PLACA
                ).apply {
                    addAll(
                        placas
                    )
                }
            }

        val adaptador =
            ArrayAdapter(
                this,
                android.R.layout
                    .simple_spinner_item,
                opciones
            )

        adaptador.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item
        )

        spnPlacasRegistradas.adapter =
            adaptador

        spnPlacasRegistradas.isEnabled =
            placas.isNotEmpty()
    }

    private fun configurarEventos() {
        btnBuscar.setOnClickListener {
            buscarVehiculo()
        }

        btnRegistrarEntrada.setOnClickListener {
            confirmarMovimiento(
                esEntrada = true
            )
        }

        btnRegistrarSalida.setOnClickListener {
            confirmarMovimiento(
                esEntrada = false
            )
        }

        btnCerrarSesion.setOnClickListener {
            confirmarCierreSesion()
        }

        btnVerHistorial.setOnClickListener {
            val intent =
                Intent(
                    this,
                    HistorialActivity::class.java
                )

            startActivity(
                intent
            )
        }
    }

    private fun buscarVehiculo() {
        val placa =
            edtPlacaBuscar.text
                .toString()
                .trim()
                .uppercase(
                    Locale.ROOT
                )

        edtPlacaBuscar.error = null

        ocultarMensajeBusqueda()

        if (placa.isEmpty()) {
            edtPlacaBuscar.error =
                "Ingrese una placa."

            edtPlacaBuscar.requestFocus()
            return
        }

        edtPlacaBuscar.setText(
            placa
        )

        edtPlacaBuscar.setSelection(
            placa.length
        )

        val vehiculo =
            seguridadRepository
                .buscarVehiculoPorPlaca(
                    placa
                )

        if (vehiculo == null) {
            vehiculoActual = null

            contenedorResultado.visibility =
                View.GONE

            btnRegistrarEntrada.visibility =
                View.GONE

            btnRegistrarSalida.visibility =
                View.GONE

            mostrarMensajeBusqueda(
                mensaje =
                    "No se encontró un vehículo con esa placa.",
                color =
                    "#C62828"
            )

            return
        }

        vehiculoActual =
            vehiculo

        mostrarResultado(
            vehiculo
        )
    }

    private fun mostrarResultado(
        vehiculo: VehiculoSeguridad
    ) {
        ocultarMensajeBusqueda()

        contenedorResultado.visibility =
            View.VISIBLE

        txtUsuarioVehiculo.text =
            vehiculo.nombreUsuario

        txtCorreoVehiculo.text =
            vehiculo.correoUsuario

        txtPlacaVehiculo.text =
            vehiculo.placa
                .uppercase(
                    Locale.ROOT
                )

        txtDetalleVehiculo.text =
            """
            Marca: ${vehiculo.marca}
            Color: ${vehiculo.color}
            Tipo: ${vehiculo.tipo}
            """.trimIndent()

        mostrarEstadoSolicitud(
            vehiculo.solicitudEstado
        )

        mostrarEstadoMovimiento(
            vehiculo
        )

        actualizarBotones(
            vehiculo
        )
    }

    private fun mostrarEstadoSolicitud(
        estado: String
    ) {
        val estadoNormalizado =
            estado.uppercase(
                Locale.ROOT
            )

        txtEstadoSolicitud.text =
            estadoNormalizado

        when (estadoNormalizado) {
            "APROBADO" -> {
                cardEstadoSolicitud
                    .setCardBackgroundColor(
                        Color.parseColor(
                            "#E8F5E9"
                        )
                    )

                txtEstadoSolicitud
                    .setTextColor(
                        Color.parseColor(
                            "#2E7D32"
                        )
                    )
            }

            "RECHAZADO" -> {
                cardEstadoSolicitud
                    .setCardBackgroundColor(
                        Color.parseColor(
                            "#FFEBEE"
                        )
                    )

                txtEstadoSolicitud
                    .setTextColor(
                        Color.parseColor(
                            "#C62828"
                        )
                    )
            }

            else -> {
                cardEstadoSolicitud
                    .setCardBackgroundColor(
                        Color.parseColor(
                            "#FFF3E0"
                        )
                    )

                txtEstadoSolicitud
                    .setTextColor(
                        Color.parseColor(
                            "#EF6C00"
                        )
                    )
            }
        }
    }

    private fun mostrarEstadoMovimiento(
        vehiculo: VehiculoSeguridad
    ) {
        if (vehiculo.estaDentro) {
            cardEstadoMovimiento
                .setCardBackgroundColor(
                    Color.parseColor(
                        "#FFF3E0"
                    )
                )

            txtEstadoMovimiento
                .setTextColor(
                    Color.parseColor(
                        "#E65100"
                    )
                )

            txtEstadoMovimiento.text =
                """
                DENTRO DEL ESTACIONAMIENTO
                Entrada: ${vehiculo.fechaEntrada ?: "-"}
                """.trimIndent()
        } else {
            cardEstadoMovimiento
                .setCardBackgroundColor(
                    Color.parseColor(
                        "#E8F5E9"
                    )
                )

            txtEstadoMovimiento
                .setTextColor(
                    Color.parseColor(
                        "#2E7D32"
                    )
                )

            txtEstadoMovimiento.text =
                "FUERA DEL ESTACIONAMIENTO"
        }
    }

    private fun actualizarBotones(
        vehiculo: VehiculoSeguridad
    ) {
        if (
            vehiculo.solicitudEstado
                .uppercase(
                    Locale.ROOT
                ) != "APROBADO"
        ) {
            btnRegistrarEntrada.visibility =
                View.GONE

            btnRegistrarSalida.visibility =
                View.GONE

            return
        }

        if (vehiculo.estaDentro) {
            btnRegistrarEntrada.visibility =
                View.GONE

            btnRegistrarSalida.visibility =
                View.VISIBLE

            return
        }

        btnRegistrarSalida.visibility =
            View.GONE

        if (
            seguridadRepository
                .estacionamientoLleno()
        ) {
            btnRegistrarEntrada.visibility =
                View.GONE

            mostrarMensajeBusqueda(
                mensaje =
                    "El estacionamiento se encuentra lleno. No es posible registrar una nueva entrada.",
                color =
                    "#C62828"
            )
        } else {
            btnRegistrarEntrada.visibility =
                View.VISIBLE
        }
    }

    private fun confirmarMovimiento(
        esEntrada: Boolean
    ) {
        val vehiculo =
            vehiculoActual ?: return

        val accion =
            if (esEntrada) {
                "entrada"
            } else {
                "salida"
            }

        AlertDialog.Builder(
            this
        )
            .setTitle(
                "Registrar $accion"
            )
            .setMessage(
                "¿Confirmas la $accion del vehículo ${vehiculo.placa}?"
            )
            .setPositiveButton(
                "CONFIRMAR"
            ) { _, _ ->
                procesarMovimiento(
                    vehiculo =
                        vehiculo,
                    esEntrada =
                        esEntrada
                )
            }
            .setNegativeButton(
                "CANCELAR",
                null
            )
            .show()
    }

    private fun procesarMovimiento(
        vehiculo: VehiculoSeguridad,
        esEntrada: Boolean
    ) {
        val resultado =
            if (esEntrada) {
                seguridadRepository
                    .registrarEntrada(
                        vehiculo.vehiculoId
                    )
            } else {
                seguridadRepository
                    .registrarSalida(
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
            return
        }

        if (
            esEntrada &&
            seguridadRepository
                .estacionamientoLleno()
        ) {
            buscarVehiculo()
        }
    }

    private fun mostrarMensajeBusqueda(
        mensaje: String,
        color: String
    ) {
        txtMensajeBusqueda.text =
            mensaje

        txtMensajeBusqueda.setTextColor(
            Color.parseColor(
                color
            )
        )

        txtMensajeBusqueda.visibility =
            View.VISIBLE
    }

    private fun ocultarMensajeBusqueda() {
        txtMensajeBusqueda.text =
            ""

        txtMensajeBusqueda.visibility =
            View.GONE
    }

    private fun ocultarResultadoActual() {
        vehiculoActual = null

        contenedorResultado.visibility =
            View.GONE

        btnRegistrarEntrada.visibility =
            View.GONE

        btnRegistrarSalida.visibility =
            View.GONE

        ocultarMensajeBusqueda()
    }

    private fun confirmarCierreSesion() {
        AlertDialog.Builder(
            this
        )
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

    companion object {
        private const val SELECCIONE_PLACA =
            "Selecciona una placa registrada"

        private const val SIN_PLACAS =
            "No hay placas registradas"
    }
}