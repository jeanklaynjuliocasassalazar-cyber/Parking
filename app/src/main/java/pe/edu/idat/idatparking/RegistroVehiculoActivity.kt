package pe.edu.idat.idatparking

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.repository.RegistroRepository
import java.util.Locale

class RegistroVehiculoActivity : AppCompatActivity() {

    private lateinit var tilPlaca: TextInputLayout
    private lateinit var tilMarca: TextInputLayout
    private lateinit var tilColor: TextInputLayout
    private lateinit var tilTipoVehiculo: TextInputLayout

    private lateinit var edtPlaca: TextInputEditText
    private lateinit var edtMarca: TextInputEditText
    private lateinit var edtColor: TextInputEditText
    private lateinit var spnTipo: AutoCompleteTextView

    private lateinit var btnGuardar: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    private lateinit var sessionManager: SessionManager
    private lateinit var registroRepository: RegistroRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_vehiculo)

        sessionManager = SessionManager(this)
        registroRepository = RegistroRepository(this)

        enlazarControles()
        configurarSelectorTipo()
        configurarEventos()
    }

    private fun enlazarControles() {
        tilPlaca = findViewById(R.id.tilPlaca)
        tilMarca = findViewById(R.id.tilMarca)
        tilColor = findViewById(R.id.tilColor)
        tilTipoVehiculo = findViewById(R.id.tilTipoVehiculo)

        edtPlaca = findViewById(R.id.edtPlaca)
        edtMarca = findViewById(R.id.edtMarca)
        edtColor = findViewById(R.id.edtColor)
        spnTipo = findViewById(R.id.spnTipoVehiculo)

        btnGuardar = findViewById(R.id.btnGuardarVehiculo)
        btnCancelar = findViewById(R.id.btnCancelarVehiculo)
    }

    private fun configurarSelectorTipo() {
        val tipos = listOf(
            "Automóvil",
            "Motocicleta",
            "Camioneta"
        )

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            tipos
        )

        spnTipo.setAdapter(adaptador)

        // Opción seleccionada inicialmente.
        spnTipo.setText(
            tipos.first(),
            false
        )

        spnTipo.setOnClickListener {
            spnTipo.showDropDown()
        }

        spnTipo.setOnFocusChangeListener { _, tieneFoco ->
            if (tieneFoco) {
                spnTipo.showDropDown()
            }
        }
    }

    private fun configurarEventos() {
        btnGuardar.setOnClickListener {
            guardarVehiculo()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun guardarVehiculo() {
        limpiarErrores()

        val placa = edtPlaca.text
            ?.toString()
            .orEmpty()
            .trim()
            .uppercase(Locale.ROOT)

        val marca = edtMarca.text
            ?.toString()
            .orEmpty()
            .trim()

        val color = edtColor.text
            ?.toString()
            .orEmpty()
            .trim()

        val tipo = spnTipo.text
            ?.toString()
            .orEmpty()
            .trim()

        if (placa.isEmpty()) {
            tilPlaca.error = "Ingrese la placa."
            edtPlaca.requestFocus()
            return
        }

        if (placa.length < 5) {
            tilPlaca.error = "Ingrese una placa válida."
            edtPlaca.requestFocus()
            return
        }

        if (marca.isEmpty()) {
            tilMarca.error = "Ingrese la marca."
            edtMarca.requestFocus()
            return
        }

        if (color.isEmpty()) {
            tilColor.error = "Ingrese el color."
            edtColor.requestFocus()
            return
        }

        if (tipo.isEmpty()) {
            tilTipoVehiculo.error =
                "Seleccione el tipo de vehículo."

            spnTipo.requestFocus()
            spnTipo.showDropDown()
            return
        }

        val usuarioId =
            sessionManager.obtenerIdUsuario()

        if (usuarioId <= 0) {
            Toast.makeText(
                this,
                "No se encontró la sesión del usuario.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // Conserva la placa normalizada en el campo.
        edtPlaca.setText(placa)

        cambiarEstadoBotones(false)

        val resultado =
            registroRepository.registrarVehiculoYSolicitud(
                usuarioId = usuarioId,
                placa = placa,
                marca = marca,
                color = color,
                tipo = tipo
            )

        cambiarEstadoBotones(true)

        Toast.makeText(
            this,
            resultado.mensaje,
            Toast.LENGTH_LONG
        ).show()

        if (resultado.exito) {
            finish()
        }
    }

    private fun limpiarErrores() {
        tilPlaca.error = null
        tilMarca.error = null
        tilColor.error = null
        tilTipoVehiculo.error = null
    }

    private fun cambiarEstadoBotones(
        habilitados: Boolean
    ) {
        btnGuardar.isEnabled = habilitados
        btnCancelar.isEnabled = habilitados
    }
}