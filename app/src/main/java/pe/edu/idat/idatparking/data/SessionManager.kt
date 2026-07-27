package pe.edu.idat.idatparking.data

import android.content.Context
import pe.edu.idat.idatparking.entity.Usuario
import java.util.Locale

class SessionManager(context: Context) {

    private val preferences =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    companion object {
        private const val PREFS_NAME =
            "sesion_idat_parking"

        private const val KEY_LOGUEADO =
            "logueado"

        private const val KEY_ID =
            "usuario_id"

        private const val KEY_NOMBRE =
            "usuario_nombre"

        private const val KEY_CORREO =
            "usuario_correo"

        private const val KEY_ROL =
            "usuario_rol"
    }

    fun guardarSesion(
        usuario: Usuario
    ) {
        preferences.edit()
            .putBoolean(
                KEY_LOGUEADO,
                true
            )
            .putInt(
                KEY_ID,
                usuario.id
            )
            .putString(
                KEY_NOMBRE,
                usuario.nombre
            )
            .putString(
                KEY_CORREO,
                usuario.correo
            )
            .putString(
                KEY_ROL,
                usuario.rol
            )
            .apply()
    }

    fun existeSesion(): Boolean {
        return preferences.getBoolean(
            KEY_LOGUEADO,
            false
        )
    }

    fun obtenerIdUsuario(): Int {
        return preferences.getInt(
            KEY_ID,
            0
        )
    }

    fun obtenerNombre(): String {
        return preferences.getString(
            KEY_NOMBRE,
            ""
        ) ?: ""
    }

    fun obtenerCorreo(): String {
        return preferences.getString(
            KEY_CORREO,
            ""
        ) ?: ""
    }

    fun obtenerRol(): String {
        return preferences.getString(
            KEY_ROL,
            ""
        ) ?: ""
    }

    fun tieneRolPermitido(
        vararg rolesPermitidos: String
    ): Boolean {
        if (!existeSesion()) {
            return false
        }

        val rolActual =
            obtenerRol()
                .trim()
                .uppercase(
                    Locale.ROOT
                )

        if (rolActual.isEmpty()) {
            return false
        }

        return rolesPermitidos.any { rol ->
            rol.trim()
                .uppercase(
                    Locale.ROOT
                ) == rolActual
        }
    }

    fun cerrarSesion() {
        preferences.edit()
            .clear()
            .apply()
    }
}