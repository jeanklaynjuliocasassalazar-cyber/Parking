package pe.edu.idat.idatparking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import pe.edu.idat.idatparking.data.SessionManager
import pe.edu.idat.idatparking.ui.AcercaFragment
import pe.edu.idat.idatparking.ui.InicioFragment
import pe.edu.idat.idatparking.ui.MiVehiculoFragment

class AlumnoActivity : AppCompatActivity() {

    private lateinit var drawerLayout:
            DrawerLayout

    private lateinit var navigationView:
            NavigationView

    private lateinit var btnAbrirMenu:
            Button

    private lateinit var txtTitulo:
            TextView

    private lateinit var sessionManager:
            SessionManager

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumno)

        sessionManager =
            SessionManager(this)

        drawerLayout =
            findViewById(
                R.id.drawerLayoutAlumno
            )

        navigationView =
            findViewById(
                R.id.navigationViewAlumno
            )

        btnAbrirMenu =
            findViewById(
                R.id.btnAbrirMenuAlumno
            )

        txtTitulo =
            findViewById(
                R.id.txtTituloAlumno
            )

        btnAbrirMenu.setOnClickListener {

            drawerLayout.openDrawer(
                GravityCompat.START
            )
        }

        navigationView
            .setNavigationItemSelectedListener { item ->

                when (item.itemId) {

                    R.id.nav_inicio -> {
                        mostrarFragmento(
                            InicioFragment(),
                            "Inicio"
                        )
                    }

                    R.id.nav_mi_vehiculo -> {
                        mostrarFragmento(
                            MiVehiculoFragment(),
                            "Mi vehículo"
                        )
                    }

                    R.id.nav_acerca -> {
                        mostrarFragmento(
                            AcercaFragment(),
                            "Acerca del proyecto"
                        )
                    }

                    R.id.nav_cerrar_sesion -> {
                        cerrarSesion()
                    }
                }

                item.isChecked = true

                drawerLayout.closeDrawer(
                    GravityCompat.START
                )

                true
            }

        if (savedInstanceState == null) {

            mostrarFragmento(
                InicioFragment(),
                "Inicio"
            )

            navigationView.setCheckedItem(
                R.id.nav_inicio
            )
        }
    }

    private fun mostrarFragmento(
        fragment: Fragment,
        titulo: String
    ) {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainerAlumno,
                fragment
            )
            .commit()

        txtTitulo.text = titulo
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

    @Deprecated(
        "Deprecated in Android, retained for this basic XML project"
    )
    override fun onBackPressed() {

        if (
            drawerLayout.isDrawerOpen(
                GravityCompat.START
            )
        ) {
            drawerLayout.closeDrawer(
                GravityCompat.START
            )
        } else {
            super.onBackPressed()
        }
    }
}