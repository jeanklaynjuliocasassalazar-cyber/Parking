package pe.edu.idat.idatparking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.entity.Usuario
import pe.edu.idat.idatparking.repository.UsuarioRepository

class MainActivity : AppCompatActivity() {

    private lateinit var edtCorreo: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnIngresar: Button
    private lateinit var txtMensaje: TextView

    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usuarioRepository = UsuarioRepository(this)
        sessionManager = SessionManager(this)

        if (sessionManager.existeSesion()) {
            abrirPantallaSegunRol(sessionManager.obtenerRol())
            return
        }

        setContentView(R.layout.activity_main)

        enlazarControles()
        configurarEventos()
    }

    private fun enlazarControles() {
        edtCorreo = findViewById(R.id.edtCorreo)
        edtPassword = findViewById(R.id.edtPassword)
        btnIngresar = findViewById(R.id.btnIngresar)
        txtMensaje = findViewById(R.id.txtMensaje)
    }

    private fun configurarEventos() {
        btnIngresar.setOnClickListener {
            realizarLogin()
        }
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
}