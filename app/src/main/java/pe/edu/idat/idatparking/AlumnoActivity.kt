package pe.edu.idat.idatparking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.entity.Usuario
import pe.edu.idat.idatparking.ui.AcercaFragment
import pe.edu.idat.idatparking.ui.InicioFragment
import pe.edu.idat.idatparking.ui.MiVehiculoFragment

class AlumnoActivity : AppCompatActivity() {

    private lateinit var drawerLayout:
            DrawerLayout

    private lateinit var navigationView:
            NavigationView

    private lateinit var txtTitulo:
            TextView

    private lateinit var btnInicioBottom:
            LinearLayout

    private lateinit var btnMenuCentro:
            LinearLayout

    private lateinit var btnSalirBottom:
            LinearLayout

    private lateinit var imgInicioBottom:
            ImageView

    private lateinit var imgMenuBottom:
            ImageView

    private lateinit var imgSalirBottom:
            ImageView

    private lateinit var txtInicioBottom:
            TextView

    private lateinit var txtMenuBottom:
            TextView

    private lateinit var txtSalirBottom:
            TextView

    private lateinit var sessionManager:
            SessionManager

    private var seccionActualId: Int =
        R.id.nav_inicio

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_alumno
        )

        sessionManager =
            SessionManager(this)

        if (
            !sessionManager
                .tieneRolPermitido(
                    Usuario.ROL_ALUMNO,
                    Usuario.ROL_DOCENTE
                )
        ) {
            sessionManager.cerrarSesion()
            regresarAlLogin()
            return
        }

        enlazarControles()
        configurarCabecera()
        configurarBarraInferior()
        configurarMenuLateral()
        configurarBotonAtras()

        if (savedInstanceState == null) {
            mostrarFragmento(
                fragment =
                    InicioFragment(),
                titulo =
                    "Inicio",
                menuItemId =
                    R.id.nav_inicio
            )
        }
    }

    private fun enlazarControles() {
        drawerLayout =
            findViewById(
                R.id.drawerLayoutAlumno
            )

        navigationView =
            findViewById(
                R.id.navigationViewAlumno
            )

        txtTitulo =
            findViewById(
                R.id.txtTituloAlumno
            )

        btnInicioBottom =
            findViewById(
                R.id.btnInicioBottom
            )

        btnMenuCentro =
            findViewById(
                R.id.btnMenuCentro
            )

        btnSalirBottom =
            findViewById(
                R.id.btnSalirBottom
            )

        imgInicioBottom =
            findViewById(
                R.id.imgInicioBottom
            )

        imgMenuBottom =
            findViewById(
                R.id.imgMenuBottom
            )

        imgSalirBottom =
            findViewById(
                R.id.imgSalirBottom
            )

        txtInicioBottom =
            findViewById(
                R.id.txtInicioBottom
            )

        txtMenuBottom =
            findViewById(
                R.id.txtMenuBottom
            )

        txtSalirBottom =
            findViewById(
                R.id.txtSalirBottom
            )
    }

    private fun configurarCabecera() {
        val headerView =
            navigationView.getHeaderView(
                0
            )

        val txtHeaderNombre =
            headerView.findViewById<TextView>(
                R.id.txtHeaderNombre
            )

        val txtHeaderCorreo =
            headerView.findViewById<TextView>(
                R.id.txtHeaderCorreo
            )

        val txtHeaderRol =
            headerView.findViewById<TextView>(
                R.id.txtHeaderRol
            )

        val nombre =
            sessionManager
                .obtenerNombre()
                .ifBlank {
                    "Usuario"
                }

        val correo =
            sessionManager
                .obtenerCorreo()
                .ifBlank {
                    "Sin correo registrado"
                }

        val rol =
            sessionManager
                .obtenerRol()
                .ifBlank {
                    "USUARIO"
                }

        txtHeaderNombre.text =
            nombre

        txtHeaderCorreo.text =
            correo

        txtHeaderRol.text =
            rol
    }

    private fun configurarBarraInferior() {
        btnInicioBottom.setOnClickListener {
            mostrarFragmento(
                fragment =
                    InicioFragment(),
                titulo =
                    "Inicio",
                menuItemId =
                    R.id.nav_inicio
            )
        }

        btnMenuCentro.setOnClickListener {
            actualizarEstadoBarraInferior(
                ESTADO_MENU
            )

            drawerLayout.openDrawer(
                GravityCompat.START
            )
        }

        btnSalirBottom.setOnClickListener {
            actualizarEstadoBarraInferior(
                ESTADO_SALIR
            )

            mostrarConfirmacionCierreSesion()
        }
    }

    private fun configurarMenuLateral() {
        navigationView
            .setNavigationItemSelectedListener { item ->

                when (item.itemId) {
                    R.id.nav_inicio -> {
                        mostrarFragmento(
                            fragment =
                                InicioFragment(),
                            titulo =
                                "Inicio",
                            menuItemId =
                                R.id.nav_inicio
                        )
                    }

                    R.id.nav_mi_vehiculo -> {
                        mostrarFragmento(
                            fragment =
                                MiVehiculoFragment(),
                            titulo =
                                "Mi vehículo",
                            menuItemId =
                                R.id.nav_mi_vehiculo
                        )
                    }

                    R.id.nav_acerca -> {
                        mostrarFragmento(
                            fragment =
                                AcercaFragment(),
                            titulo =
                                "Acerca del proyecto",
                            menuItemId =
                                R.id.nav_acerca
                        )
                    }

                    R.id.nav_cerrar_sesion -> {
                        mostrarConfirmacionCierreSesion()
                    }
                }

                drawerLayout.closeDrawer(
                    GravityCompat.START
                )

                true
            }
    }

    private fun mostrarFragmento(
        fragment: Fragment,
        titulo: String,
        menuItemId: Int
    ) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(
                R.id.fragmentContainerAlumno,
                fragment
            )
            .commit()

        txtTitulo.text =
            titulo

        seccionActualId =
            menuItemId

        navigationView.setCheckedItem(
            menuItemId
        )

        actualizarEstadoBarraInferior(
            if (
                menuItemId ==
                R.id.nav_inicio
            ) {
                ESTADO_INICIO
            } else {
                ESTADO_MENU
            }
        )
    }

    private fun actualizarEstadoBarraInferior(
        opcionSeleccionada: String
    ) {
        val colorActivo =
            Color.parseColor(
                "#512DA8"
            )

        val colorInactivo =
            Color.parseColor(
                "#A0A0A0"
            )

        val colorSalir =
            Color.parseColor(
                "#C62828"
            )

        imgInicioBottom.setColorFilter(
            colorInactivo
        )

        txtInicioBottom.setTextColor(
            colorInactivo
        )

        imgMenuBottom.setColorFilter(
            colorInactivo
        )

        txtMenuBottom.setTextColor(
            colorInactivo
        )

        imgSalirBottom.setColorFilter(
            colorInactivo
        )

        txtSalirBottom.setTextColor(
            colorInactivo
        )

        when (opcionSeleccionada) {
            ESTADO_INICIO -> {
                imgInicioBottom
                    .setColorFilter(
                        colorActivo
                    )

                txtInicioBottom
                    .setTextColor(
                        colorActivo
                    )

                txtInicioBottom
                    .setTypeface(
                        null,
                        android.graphics
                            .Typeface.BOLD
                    )
            }

            ESTADO_MENU -> {
                imgMenuBottom
                    .setColorFilter(
                        colorActivo
                    )

                txtMenuBottom
                    .setTextColor(
                        colorActivo
                    )

                txtMenuBottom
                    .setTypeface(
                        null,
                        android.graphics
                            .Typeface.BOLD
                    )
            }

            ESTADO_SALIR -> {
                imgSalirBottom
                    .setColorFilter(
                        colorSalir
                    )

                txtSalirBottom
                    .setTextColor(
                        colorSalir
                    )

                txtSalirBottom
                    .setTypeface(
                        null,
                        android.graphics
                            .Typeface.BOLD
                    )
            }
        }
    }

    private fun restaurarEstadoBarraInferior() {
        val estado =
            if (
                seccionActualId ==
                R.id.nav_inicio
            ) {
                ESTADO_INICIO
            } else {
                ESTADO_MENU
            }

        actualizarEstadoBarraInferior(
            estado
        )
    }

    private fun mostrarConfirmacionCierreSesion() {
        val dialogo =
            AlertDialog.Builder(this)
                .setTitle(
                    "Cerrar sesión"
                )
                .setMessage(
                    "¿Está seguro de que desea salir de IDAT Parking?"
                )
                .setPositiveButton(
                    "Sí"
                ) { _, _ ->
                    cerrarSesion()
                }
                .setNegativeButton(
                    "No"
                ) { _, _ ->
                    restaurarEstadoBarraInferior()
                }
                .create()

        dialogo.setOnCancelListener {
            restaurarEstadoBarraInferior()
        }

        dialogo.show()
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

    private fun configurarBotonAtras() {
        onBackPressedDispatcher
            .addCallback(
                this,
                object :
                    OnBackPressedCallback(
                        true
                    ) {

                    override fun handleOnBackPressed() {
                        if (
                            drawerLayout
                                .isDrawerOpen(
                                    GravityCompat.START
                                )
                        ) {
                            drawerLayout
                                .closeDrawer(
                                    GravityCompat.START
                                )

                            restaurarEstadoBarraInferior()
                            return
                        }

                        if (
                            seccionActualId !=
                            R.id.nav_inicio
                        ) {
                            mostrarFragmento(
                                fragment =
                                    InicioFragment(),
                                titulo =
                                    "Inicio",
                                menuItemId =
                                    R.id.nav_inicio
                            )

                            return
                        }

                        isEnabled =
                            false

                        onBackPressedDispatcher
                            .onBackPressed()
                    }
                }
            )
    }

    companion object {
        private const val ESTADO_INICIO =
            "INICIO"

        private const val ESTADO_MENU =
            "MENU"

        private const val ESTADO_SALIR =
            "SALIR"
    }
}