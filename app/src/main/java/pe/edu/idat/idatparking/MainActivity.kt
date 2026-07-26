package pe.edu.idat.idatparking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.entity.Usuario
import pe.edu.idat.idatparking.repository.UsuarioRepository

class MainActivity : AppCompatActivity() {

    private lateinit var edtCorreo: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var btnIngresar: MaterialButton
    private lateinit var txtMensaje: TextView
    private lateinit var rgCredenciales: RadioGroup

    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usuarioRepository = UsuarioRepository(this)
        sessionManager = SessionManager(this)

        enlazarControles()
        configurarEventos()

        if (sessionManager.existeSesion()) {
            abrirPantallaSegunRol(sessionManager.obtenerRol())
        }
    }

    private fun enlazarControles() {
        edtCorreo = findViewById(R.id.edtCorreo)
        edtPassword = findViewById(R.id.edtPassword)
        btnIngresar = findViewById(R.id.btnIngresar)
        txtMensaje = findViewById(R.id.txtMensaje)
        rgCredenciales = findViewById(R.id.rgCredenciales)
    }

    private fun configurarEventos() {
        btnIngresar.setOnClickListener {
            realizarLogin()
        }

        rgCredenciales.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbAlumno -> {
                    autocompletarCredenciales(CORREO_ALUMNO)
                }

                R.id.rbDocente -> {
                    autocompletarCredenciales(CORREO_DOCENTE)
                }

                R.id.rbSupervisor -> {
                    autocompletarCredenciales(CORREO_SUPERVISOR)
                }

                R.id.rbSeguridad -> {
                    autocompletarCredenciales(CORREO_SEGURIDAD)
                }
            }
        }
    }

    private fun autocompletarCredenciales(correo: String) {
        edtCorreo.setText(correo)
        edtPassword.setText(PASSWORD_DEMO)

        edtCorreo.setSelection(edtCorreo.text?.length ?: 0)
        edtPassword.setSelection(edtPassword.text?.length ?: 0)

        ocultarMensaje()
    }

    private fun realizarLogin() {
        val correo = edtCorreo.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        ocultarMensaje()

        if (correo.isEmpty()) {
            mostrarMensaje("Ingrese el correo institucional.")
            edtCorreo.requestFocus()
            return
        }

        if (password.isEmpty()) {
            mostrarMensaje("Ingrese la contraseña.")
            edtPassword.requestFocus()
            return
        }

        val usuario = usuarioRepository.login(
            correo = correo,
            password = password
        )

        if (usuario == null) {
            mostrarMensaje("Correo o contraseña incorrectos.")
            return
        }

        sessionManager.guardarSesion(usuario)
        abrirPantallaSegunRol(usuario.rol)
    }

    private fun abrirPantallaSegunRol(rol: String) {
        val destino = when (rol) {
            Usuario.ROL_SUPERVISOR -> SupervisorActivity::class.java
            Usuario.ROL_SEGURIDAD -> SeguridadActivity::class.java

            Usuario.ROL_ALUMNO,
            Usuario.ROL_DOCENTE -> AlumnoActivity::class.java

            else -> null
        }

        if (destino == null) {
            sessionManager.cerrarSesion()
            mostrarMensaje("El usuario no tiene un rol válido.")
            return
        }

        val intent = Intent(this, destino).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }

    private fun mostrarMensaje(mensaje: String) {
        txtMensaje.text = mensaje
        txtMensaje.visibility = View.VISIBLE
    }

    private fun ocultarMensaje() {
        txtMensaje.text = ""
        txtMensaje.visibility = View.GONE
    }

    companion object {
        private const val CORREO_ALUMNO = "a76543210@idat.pe"
        private const val CORREO_DOCENTE = "d72345678@idat.pe"
        private const val CORREO_SUPERVISOR = "s74521890@idat.pe"
        private const val CORREO_SEGURIDAD = "seguridad01@idat.pe"
        private const val PASSWORD_DEMO = "1234"
    }
}