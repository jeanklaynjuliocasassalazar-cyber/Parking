package pe.edu.idat.idatparking

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import pe.edu.idat.idatparking.adapter.MovimientoAdapter
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.entity.Usuario
import pe.edu.idat.idatparking.repository.MovimientoRepository
import java.util.Calendar
import java.util.Locale

class HistorialActivity : AppCompatActivity() {

    private lateinit var sessionManager:
            SessionManager

    private lateinit var movimientoRepository:
            MovimientoRepository

    private lateinit var movimientoAdapter:
            MovimientoAdapter

    private lateinit var txtResumen: TextView
    private lateinit var txtSinMovimientos: TextView
    private lateinit var txtTotalMovimientos: TextView

    private lateinit var edtFiltroPlaca:
            TextInputEditText

    private lateinit var spnEstado:
            AutoCompleteTextView

    private lateinit var edtFecha:
            TextInputEditText

    private lateinit var btnAplicarFiltros:
            MaterialButton

    private lateinit var btnLimpiarFiltros:
            MaterialButton

    private lateinit var rvHistorial:
            RecyclerView

    private lateinit var btnVolver:
            Button

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        sessionManager =
            SessionManager(this)

        if (!validarAcceso()) {
            return
        }

        movimientoRepository =
            MovimientoRepository(this)

        enlazarControles()
        configurarEstado()
        configurarFecha()
        configurarRecyclerView()
        configurarEventos()
    }

    override fun onResume() {
        super.onResume()

        if (!validarAcceso()) {
            return
        }

        if (
            ::movimientoRepository.isInitialized
        ) {
            cargarInformacion()
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
        txtResumen =
            findViewById(
                R.id.txtResumenHistorial
            )

        txtSinMovimientos =
            findViewById(
                R.id.txtSinMovimientos
            )

        txtTotalMovimientos =
            findViewById(
                R.id.txtTotalMovimientosHistorial
            )

        edtFiltroPlaca =
            findViewById(
                R.id.edtFiltroPlacaHistorial
            )

        spnEstado =
            findViewById(
                R.id.spnEstadoHistorial
            )

        edtFecha =
            findViewById(
                R.id.edtFechaHistorial
            )

        btnAplicarFiltros =
            findViewById(
                R.id.btnAplicarFiltrosHistorial
            )

        btnLimpiarFiltros =
            findViewById(
                R.id.btnLimpiarFiltrosHistorial
            )

        rvHistorial =
            findViewById(
                R.id.rvHistorial
            )

        btnVolver =
            findViewById(
                R.id.btnVolverHistorial
            )
    }

    private fun configurarEstado() {
        val estados =
            listOf(
                "TODOS",
                "DENTRO",
                "FINALIZADO"
            )

        val adaptador =
            ArrayAdapter(
                this,
                android.R.layout
                    .simple_dropdown_item_1line,
                estados
            )

        spnEstado.setAdapter(
            adaptador
        )

        spnEstado.setText(
            estados.first(),
            false
        )

        spnEstado.setOnClickListener {
            spnEstado.showDropDown()
        }

        spnEstado.setOnFocusChangeListener { _, tieneFoco ->
            if (tieneFoco) {
                spnEstado.showDropDown()
            }
        }
    }

    private fun configurarFecha() {
        edtFecha.setOnClickListener {
            mostrarSelectorFecha()
        }
    }

    private fun configurarRecyclerView() {
        movimientoAdapter =
            MovimientoAdapter(
                emptyList()
            )

        rvHistorial.layoutManager =
            LinearLayoutManager(this)

        rvHistorial.adapter =
            movimientoAdapter
    }

    private fun configurarEventos() {
        btnAplicarFiltros.setOnClickListener {
            cargarInformacion()
        }

        btnLimpiarFiltros.setOnClickListener {
            limpiarFiltros()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun mostrarSelectorFecha() {
        val calendario =
            Calendar.getInstance()

        val dialogo =
            DatePickerDialog(
                this,
                { _, anio, mes, dia ->
                    val fecha =
                        String.format(
                            Locale.ROOT,
                            "%04d-%02d-%02d",
                            anio,
                            mes + 1,
                            dia
                        )

                    edtFecha.setText(
                        fecha
                    )
                },
                calendario.get(
                    Calendar.YEAR
                ),
                calendario.get(
                    Calendar.MONTH
                ),
                calendario.get(
                    Calendar.DAY_OF_MONTH
                )
            )

        dialogo.show()
    }

    private fun cargarInformacion() {
        val estadistica =
            movimientoRepository
                .obtenerEstadistica()

        txtResumen.text =
            """
            Capacidad total: ${estadistica.capacidadTotal}
            Vehículos dentro: ${estadistica.ocupados}
            Espacios disponibles: ${estadistica.disponibles}
            """.trimIndent()

        val placa =
            edtFiltroPlaca.text
                ?.toString()
                .orEmpty()
                .trim()

        val estado =
            spnEstado.text
                ?.toString()
                .orEmpty()
                .trim()

        val fecha =
            edtFecha.text
                ?.toString()
                .orEmpty()
                .trim()

        val movimientos =
            movimientoRepository
                .listarHistorial(
                    filtroPlaca = placa,
                    estado = estado,
                    fecha = fecha
                )

        movimientoAdapter.actualizarLista(
            movimientos
        )

        txtTotalMovimientos.text =
            "Total de movimientos: ${movimientos.size}"

        actualizarEstadoLista(
            movimientosVacios =
                movimientos.isEmpty(),
            filtrosAplicados =
                existenFiltrosAplicados(
                    placa = placa,
                    estado = estado,
                    fecha = fecha
                )
        )
    }

    private fun actualizarEstadoLista(
        movimientosVacios: Boolean,
        filtrosAplicados: Boolean
    ) {
        if (movimientosVacios) {
            txtSinMovimientos.text =
                if (filtrosAplicados) {
                    "No se encontraron movimientos con los filtros seleccionados."
                } else {
                    "Todavía no existen movimientos registrados."
                }

            txtSinMovimientos.visibility =
                View.VISIBLE

            rvHistorial.visibility =
                View.GONE
        } else {
            txtSinMovimientos.visibility =
                View.GONE

            rvHistorial.visibility =
                View.VISIBLE
        }
    }

    private fun existenFiltrosAplicados(
        placa: String,
        estado: String,
        fecha: String
    ): Boolean {
        return placa.isNotEmpty() ||
                fecha.isNotEmpty() ||
                (
                        estado.isNotEmpty() &&
                                estado.uppercase(
                                    Locale.ROOT
                                ) != "TODOS"
                        )
    }

    private fun limpiarFiltros() {
        edtFiltroPlaca.setText(
            ""
        )

        spnEstado.setText(
            "TODOS",
            false
        )

        edtFecha.setText(
            ""
        )

        cargarInformacion()
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
}