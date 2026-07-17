package pe.edu.idat.idatparking

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.repository.RegistroRepository

class RegistroVehiculoActivity : AppCompatActivity() {

    private lateinit var edtPlaca: EditText
    private lateinit var edtMarca: EditText
    private lateinit var edtColor: EditText
    private lateinit var spnTipo: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private lateinit var sessionManager: SessionManager
    private lateinit var registroRepository: RegistroRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_vehiculo)

        sessionManager = SessionManager(this)
        registroRepository = RegistroRepository(this)

        enlazarControles()
        configurarSpinner()
        configurarEventos()
    }

    private fun enlazarControles() {
        edtPlaca = findViewById(R.id.edtPlaca)
        edtMarca = findViewById(R.id.edtMarca)
        edtColor = findViewById(R.id.edtColor)
        spnTipo = findViewById(R.id.spnTipoVehiculo)
        btnGuardar = findViewById(R.id.btnGuardarVehiculo)
        btnCancelar = findViewById(R.id.btnCancelarVehiculo)
    }

    private fun configurarSpinner() {
        val tipos = listOf(
            "Automóvil",
            "Motocicleta",
            "Camioneta"
        )

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tipos
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spnTipo.adapter = adaptador
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
        val placa = edtPlaca.text.toString().trim()
        val marca = edtMarca.text.toString().trim()
        val color = edtColor.text.toString().trim()
        val tipo = spnTipo.selectedItem.toString()

        if (placa.isEmpty()) {
            edtPlaca.error = "Ingrese la placa."
            edtPlaca.requestFocus()
            return
        }

        if (placa.length < 5) {
            edtPlaca.error = "Ingrese una placa válida."
            edtPlaca.requestFocus()
            return
        }

        if (marca.isEmpty()) {
            edtMarca.error = "Ingrese la marca."
            edtMarca.requestFocus()
            return
        }

        if (color.isEmpty()) {
            edtColor.error = "Ingrese el color."
            edtColor.requestFocus()
            return
        }

        val usuarioId = sessionManager.obtenerIdUsuario()

        if (usuarioId <= 0) {
            Toast.makeText(
                this,
                "No se encontró la sesión del usuario.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        btnGuardar.isEnabled = false

        val resultado = registroRepository.registrarVehiculoYSolicitud(
            usuarioId = usuarioId,
            placa = placa,
            marca = marca,
            color = color,
            tipo = tipo
        )

        btnGuardar.isEnabled = true

        Toast.makeText(
            this,
            resultado.mensaje,
            Toast.LENGTH_LONG
        ).show()

        if (resultado.exito) {
            finish()
        }
    }
}