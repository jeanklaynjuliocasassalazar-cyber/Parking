package pe.edu.idat.idatparking.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import pe.edu.idat.idatparking.data.AppDatabaseHelper
import pe.edu.idat.idatparking.entity.SolicitudDetalle
import java.util.Locale

class RegistroRepository(context: Context) {

    private val dbHelper =
        AppDatabaseHelper(context.applicationContext)

    fun obtenerSolicitudPorUsuario(
        usuarioId: Int
    ): SolicitudDetalle? {

        val db =
            dbHelper.readableDatabase

        val consulta = """
            SELECT
                s.id AS solicitud_id,
                v.id AS vehiculo_id,
                v.placa,
                v.marca,
                v.color,
                v.tipo,
                s.estado,
                s.fecha_solicitud,
                s.observacion
            FROM solicitudes s
            INNER JOIN vehiculos v
                ON v.id = s.vehiculo_id
            WHERE s.usuario_id = ?
            ORDER BY s.id DESC
            LIMIT 1
        """.trimIndent()

        val cursor =
            db.rawQuery(
                consulta,
                arrayOf(
                    usuarioId.toString()
                )
            )

        cursor.use {
            if (!it.moveToFirst()) {
                return null
            }

            val indiceObservacion =
                it.getColumnIndexOrThrow(
                    "observacion"
                )

            val observacion =
                if (
                    it.isNull(
                        indiceObservacion
                    )
                ) {
                    null
                } else {
                    it.getString(
                        indiceObservacion
                    )
                }

            return SolicitudDetalle(
                solicitudId = it.getInt(
                    it.getColumnIndexOrThrow(
                        "solicitud_id"
                    )
                ),
                vehiculoId = it.getInt(
                    it.getColumnIndexOrThrow(
                        "vehiculo_id"
                    )
                ),
                placa = it.getString(
                    it.getColumnIndexOrThrow(
                        "placa"
                    )
                ),
                marca = it.getString(
                    it.getColumnIndexOrThrow(
                        "marca"
                    )
                ),
                color = it.getString(
                    it.getColumnIndexOrThrow(
                        "color"
                    )
                ),
                tipo = it.getString(
                    it.getColumnIndexOrThrow(
                        "tipo"
                    )
                ),
                estado = it.getString(
                    it.getColumnIndexOrThrow(
                        "estado"
                    )
                ),
                fechaSolicitud = it.getString(
                    it.getColumnIndexOrThrow(
                        "fecha_solicitud"
                    )
                ),
                observacion = observacion
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

        val placaNormalizada =
            placa
                .trim()
                .uppercase(
                    Locale.ROOT
                )

        val db =
            dbHelper.writableDatabase

        db.beginTransaction()

        val resultado =
            try {
                when {
                    existeVehiculoDelUsuario(
                        db,
                        usuarioId
                    ) -> {
                        ResultadoRegistro(
                            exito = false,
                            mensaje = "El usuario ya tiene un vehículo registrado."
                        )
                    }

                    existePlaca(
                        db,
                        placaNormalizada
                    ) -> {
                        ResultadoRegistro(
                            exito = false,
                            mensaje = "La placa ingresada ya está registrada."
                        )
                    }

                    else -> {
                        val valoresVehiculo =
                            ContentValues().apply {
                                put(
                                    "usuario_id",
                                    usuarioId
                                )

                                put(
                                    "placa",
                                    placaNormalizada
                                )

                                put(
                                    "marca",
                                    marca.trim()
                                )

                                put(
                                    "color",
                                    color.trim()
                                )

                                put(
                                    "tipo",
                                    tipo.trim()
                                )
                            }

                        val vehiculoId =
                            db.insertOrThrow(
                                AppDatabaseHelper
                                    .TABLA_VEHICULOS,
                                null,
                                valoresVehiculo
                            )

                        val valoresSolicitud =
                            ContentValues().apply {
                                put(
                                    "usuario_id",
                                    usuarioId
                                )

                                put(
                                    "vehiculo_id",
                                    vehiculoId
                                )

                                put(
                                    "estado",
                                    "PENDIENTE"
                                )
                            }

                        db.insertOrThrow(
                            AppDatabaseHelper
                                .TABLA_SOLICITUDES,
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

    fun actualizarVehiculoYReenviarSolicitud(
        usuarioId: Int,
        vehiculoId: Int,
        solicitudId: Int,
        placa: String,
        marca: String,
        color: String,
        tipo: String
    ): ResultadoRegistro {

        val placaNormalizada =
            placa
                .trim()
                .uppercase(
                    Locale.ROOT
                )

        val db =
            dbHelper.writableDatabase

        db.beginTransaction()

        val resultado =
            try {
                when {
                    !solicitudRechazadaPerteneceAlUsuario(
                        db = db,
                        usuarioId = usuarioId,
                        vehiculoId = vehiculoId,
                        solicitudId = solicitudId
                    ) -> {
                        ResultadoRegistro(
                            exito = false,
                            mensaje = "La solicitud no puede ser corregida o reenviada."
                        )
                    }

                    existePlacaEnOtroVehiculo(
                        db = db,
                        placa = placaNormalizada,
                        vehiculoId = vehiculoId
                    ) -> {
                        ResultadoRegistro(
                            exito = false,
                            mensaje = "La placa ingresada ya está registrada."
                        )
                    }

                    else -> {
                        val valoresVehiculo =
                            ContentValues().apply {
                                put(
                                    "placa",
                                    placaNormalizada
                                )

                                put(
                                    "marca",
                                    marca.trim()
                                )

                                put(
                                    "color",
                                    color.trim()
                                )

                                put(
                                    "tipo",
                                    tipo.trim()
                                )
                            }

                        val vehiculoActualizado =
                            db.update(
                                AppDatabaseHelper
                                    .TABLA_VEHICULOS,
                                valoresVehiculo,
                                "id = ? AND usuario_id = ?",
                                arrayOf(
                                    vehiculoId.toString(),
                                    usuarioId.toString()
                                )
                            )

                        if (
                            vehiculoActualizado <= 0
                        ) {
                            ResultadoRegistro(
                                exito = false,
                                mensaje = "No se pudieron actualizar los datos del vehículo."
                            )
                        } else {
                            val sentencia =
                                db.compileStatement(
                                    """
                                    UPDATE solicitudes
                                    SET
                                        estado = 'PENDIENTE',
                                        fecha_solicitud = CURRENT_TIMESTAMP,
                                        observacion = NULL
                                    WHERE id = ?
                                    AND usuario_id = ?
                                    AND vehiculo_id = ?
                                    AND estado = 'RECHAZADO'
                                    """.trimIndent()
                                )

                            sentencia.bindLong(
                                1,
                                solicitudId.toLong()
                            )

                            sentencia.bindLong(
                                2,
                                usuarioId.toLong()
                            )

                            sentencia.bindLong(
                                3,
                                vehiculoId.toLong()
                            )

                            val solicitudActualizada =
                                sentencia.executeUpdateDelete()

                            if (
                                solicitudActualizada <= 0
                            ) {
                                ResultadoRegistro(
                                    exito = false,
                                    mensaje = "No se pudo reenviar la solicitud."
                                )
                            } else {
                                db.setTransactionSuccessful()

                                ResultadoRegistro(
                                    exito = true,
                                    mensaje = "Datos actualizados. La solicitud fue reenviada y quedó pendiente."
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                ResultadoRegistro(
                    exito = false,
                    mensaje = "No se pudo actualizar la información: ${e.message}"
                )
            } finally {
                db.endTransaction()
            }

        return resultado
    }

    private fun solicitudRechazadaPerteneceAlUsuario(
        db: SQLiteDatabase,
        usuarioId: Int,
        vehiculoId: Int,
        solicitudId: Int
    ): Boolean {

        val consulta = """
            SELECT 1
            FROM solicitudes
            WHERE id = ?
            AND usuario_id = ?
            AND vehiculo_id = ?
            AND estado = 'RECHAZADO'
            LIMIT 1
        """.trimIndent()

        return existeRegistro(
            db = db,
            consulta = consulta,
            argumentos = arrayOf(
                solicitudId.toString(),
                usuarioId.toString(),
                vehiculoId.toString()
            )
        )
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
            db = db,
            consulta = consulta,
            argumentos = arrayOf(
                usuarioId.toString()
            )
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
            db = db,
            consulta = consulta,
            argumentos = arrayOf(
                placa
            )
        )
    }

    private fun existePlacaEnOtroVehiculo(
        db: SQLiteDatabase,
        placa: String,
        vehiculoId: Int
    ): Boolean {

        val consulta = """
            SELECT 1
            FROM vehiculos
            WHERE UPPER(placa) = ?
            AND id != ?
            LIMIT 1
        """.trimIndent()

        return existeRegistro(
            db = db,
            consulta = consulta,
            argumentos = arrayOf(
                placa,
                vehiculoId.toString()
            )
        )
    }

    private fun existeRegistro(
        db: SQLiteDatabase,
        consulta: String,
        argumentos: Array<String>
    ): Boolean {

        val cursor =
            db.rawQuery(
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