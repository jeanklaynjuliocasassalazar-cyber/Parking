package pe.edu.idat.idatparking.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import pe.edu.idat.idatparking.data.AppDatabaseHelper
import pe.edu.idat.idatparking.entity.SolicitudDetalle
import java.util.Locale

class RegistroRepository(context: Context) {

    private val dbHelper = AppDatabaseHelper(context.applicationContext)

    fun obtenerSolicitudPorUsuario(usuarioId: Int): SolicitudDetalle? {
        val db = dbHelper.readableDatabase

        val consulta = """
            SELECT
                s.id AS solicitud_id,
                v.id AS vehiculo_id,
                v.placa,
                v.marca,
                v.color,
                v.tipo,
                s.estado,
                s.fecha_solicitud
            FROM solicitudes s
            INNER JOIN vehiculos v
                ON v.id = s.vehiculo_id
            WHERE s.usuario_id = ?
            ORDER BY s.id DESC
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(
            consulta,
            arrayOf(usuarioId.toString())
        )

        cursor.use {
            if (!it.moveToFirst()) {
                return null
            }

            return SolicitudDetalle(
                solicitudId = it.getInt(
                    it.getColumnIndexOrThrow("solicitud_id")
                ),
                vehiculoId = it.getInt(
                    it.getColumnIndexOrThrow("vehiculo_id")
                ),
                placa = it.getString(
                    it.getColumnIndexOrThrow("placa")
                ),
                marca = it.getString(
                    it.getColumnIndexOrThrow("marca")
                ),
                color = it.getString(
                    it.getColumnIndexOrThrow("color")
                ),
                tipo = it.getString(
                    it.getColumnIndexOrThrow("tipo")
                ),
                estado = it.getString(
                    it.getColumnIndexOrThrow("estado")
                ),
                fechaSolicitud = it.getString(
                    it.getColumnIndexOrThrow("fecha_solicitud")
                )
            )
        }
    }

    fun registrarVehiculoYSolicitud(
        usuarioId: Int,
        placa: String,
        marca: String,
        color: String,
        tipo: String
    ): ResultadoRegistro {

        val placaNormalizada = placa
            .trim()
            .uppercase(Locale.ROOT)

        val db = dbHelper.writableDatabase
        db.beginTransaction()

        val resultado = try {

            when {
                existeVehiculoDelUsuario(db, usuarioId) -> {
                    ResultadoRegistro(
                        exito = false,
                        mensaje = "El usuario ya tiene un vehículo registrado."
                    )
                }

                existePlaca(db, placaNormalizada) -> {
                    ResultadoRegistro(
                        exito = false,
                        mensaje = "La placa ingresada ya está registrada."
                    )
                }

                else -> {
                    val valoresVehiculo = ContentValues().apply {
                        put("usuario_id", usuarioId)
                        put("placa", placaNormalizada)
                        put("marca", marca.trim())
                        put("color", color.trim())
                        put("tipo", tipo.trim())
                    }

                    val vehiculoId = db.insertOrThrow(
                        AppDatabaseHelper.TABLA_VEHICULOS,
                        null,
                        valoresVehiculo
                    )

                    val valoresSolicitud = ContentValues().apply {
                        put("usuario_id", usuarioId)
                        put("vehiculo_id", vehiculoId)
                        put("estado", "PENDIENTE")
                    }

                    db.insertOrThrow(
                        AppDatabaseHelper.TABLA_SOLICITUDES,
                        null,
                        valoresSolicitud
                    )

                    db.setTransactionSuccessful()

                    ResultadoRegistro(
                        exito = true,
                        mensaje = "Vehículo registrado. La solicitud quedó pendiente."
                    )
                }
            }

        } catch (e: Exception) {
            ResultadoRegistro(
                exito = false,
                mensaje = "No se pudo guardar la información: ${e.message}"
            )
        } finally {
            db.endTransaction()
        }

        return resultado
    }

    private fun existeVehiculoDelUsuario(
        db: SQLiteDatabase,
        usuarioId: Int
    ): Boolean {

        val consulta = """
            SELECT 1
            FROM vehiculos
            WHERE usuario_id = ?
            LIMIT 1
        """.trimIndent()

        return existeRegistro(
            db,
            consulta,
            arrayOf(usuarioId.toString())
        )
    }

    private fun existePlaca(
        db: SQLiteDatabase,
        placa: String
    ): Boolean {

        val consulta = """
            SELECT 1
            FROM vehiculos
            WHERE UPPER(placa) = ?
            LIMIT 1
        """.trimIndent()

        return existeRegistro(
            db,
            consulta,
            arrayOf(placa)
        )
    }

    private fun existeRegistro(
        db: SQLiteDatabase,
        consulta: String,
        argumentos: Array<String>
    ): Boolean {

        val cursor = db.rawQuery(
            consulta,
            argumentos
        )

        cursor.use {
            return it.moveToFirst()
        }
    }
}

data class ResultadoRegistro(
    val exito: Boolean,
    val mensaje: String
)