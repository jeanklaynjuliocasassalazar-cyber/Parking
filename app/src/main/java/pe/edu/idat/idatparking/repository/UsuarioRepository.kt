package pe.edu.idat.idatparking.repository

import android.content.Context
import pe.edu.idat.idatparking.data.AppDatabaseHelper
import pe.edu.idat.idatparking.entity.Usuario

class UsuarioRepository(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    fun login(
        correo: String,
        password: String
    ): Usuario? {

        val db = dbHelper.readableDatabase

        val columnas = arrayOf(
            AppDatabaseHelper.USUARIO_ID,
            AppDatabaseHelper.USUARIO_NOMBRE,
            AppDatabaseHelper.USUARIO_CORREO,
            AppDatabaseHelper.USUARIO_ROL
        )

        val seleccion = """
            ${AppDatabaseHelper.USUARIO_CORREO} = ?
            AND ${AppDatabaseHelper.USUARIO_PASSWORD} = ?
        """.trimIndent()

        val argumentos = arrayOf(
            correo.trim(),
            password.trim()
        )

        val cursor = db.query(
            AppDatabaseHelper.TABLA_USUARIOS,
            columnas,
            seleccion,
            argumentos,
            null,
            null,
            null
        )

        cursor.use {
            if (!it.moveToFirst()) {
                return null
            }

            return Usuario(
                id = it.getInt(
                    it.getColumnIndexOrThrow(
                        AppDatabaseHelper.USUARIO_ID
                    )
                ),
                nombre = it.getString(
                    it.getColumnIndexOrThrow(
                        AppDatabaseHelper.USUARIO_NOMBRE
                    )
                ),
                correo = it.getString(
                    it.getColumnIndexOrThrow(
                        AppDatabaseHelper.USUARIO_CORREO
                    )
                ),
                rol = it.getString(
                    it.getColumnIndexOrThrow(
                        AppDatabaseHelper.USUARIO_ROL
                    )
                )
            )
        }
    }

    fun obtenerUsuarioPorId(id: Int): Usuario? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            AppDatabaseHelper.TABLA_USUARIOS,
            arrayOf(
                AppDatabaseHelper.USUARIO_ID,
                AppDatabaseHelper.USUARIO_NOMBRE,
                AppDatabaseHelper.USUARIO_CORREO,
                AppDatabaseHelper.USUARIO_ROL
            ),
            "${AppDatabaseHelper.USUARIO_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (!it.moveToFirst()) {
                return null
            }

            return Usuario(
                id = it.getInt(
                    it.getColumnIndexOrThrow(
                        AppDatabaseHelper.USUARIO_ID
                    )
                ),
                nombre = it.getString(
                    it.getColumnIndexOrThrow(
                        AppDatabaseHelper.USUARIO_NOMBRE
                    )
                ),
                correo = it.getString(
                    it.getColumnIndexOrThrow(
                        AppDatabaseHelper.USUARIO_CORREO
                    )
                ),
                rol = it.getString(
                    it.getColumnIndexOrThrow(
                        AppDatabaseHelper.USUARIO_ROL
                    )
                )
            )
        }
    }
}