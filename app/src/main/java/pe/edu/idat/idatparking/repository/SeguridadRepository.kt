package pe.edu.idat.idatparking.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import pe.edu.idat.idatparking.data.AppDatabaseHelper
import pe.edu.idat.idatparking.entity.VehiculoSeguridad
import java.util.Locale

class SeguridadRepository(context: Context) {

    private val dbHelper =
        AppDatabaseHelper(context.applicationContext)

    fun buscarVehiculoPorPlaca(
        placa: String
    ): VehiculoSeguridad? {

        val placaNormalizada = placa
            .trim()
            .uppercase(Locale.ROOT)

        val db = dbHelper.readableDatabase

        val consulta = """
            SELECT
                v.id AS vehiculo_id,
                u.id AS usuario_id,
                u.nombre AS usuario_nombre,
                u.correo AS usuario_correo,
                v.placa,
                v.marca,
                v.color,
                v.tipo,
                s.estado AS solicitud_estado,
                m.id AS movimiento_id,
                m.fecha_entrada
            FROM vehiculos v
            INNER JOIN usuarios u
                ON u.id = v.usuario_id
            INNER JOIN solicitudes s
                ON s.vehiculo_id = v.id
            LEFT JOIN movimientos m
                ON m.vehiculo_id = v.id
                AND m.estado = 'DENTRO'
            WHERE UPPER(v.placa) = ?
            ORDER BY s.id DESC, m.id DESC
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(
            consulta,
            arrayOf(placaNormalizada)
        )

        cursor.use {
            if (!it.moveToFirst()) {
                return null
            }

            val indiceMovimiento =
                it.getColumnIndexOrThrow("movimiento_id")

            val indiceFechaEntrada =
                it.getColumnIndexOrThrow("fecha_entrada")

            val movimientoId =
                if (it.isNull(indiceMovimiento)) {
                    null
                } else {
                    it.getInt(indiceMovimiento)
                }

            val fechaEntrada =
                if (it.isNull(indiceFechaEntrada)) {
                    null
                } else {
                    it.getString(indiceFechaEntrada)
                }

            return VehiculoSeguridad(
                vehiculoId = it.getInt(
                    it.getColumnIndexOrThrow("vehiculo_id")
                ),
                usuarioId = it.getInt(
                    it.getColumnIndexOrThrow("usuario_id")
                ),
                nombreUsuario = it.getString(
                    it.getColumnIndexOrThrow("usuario_nombre")
                ),
                correoUsuario = it.getString(
                    it.getColumnIndexOrThrow("usuario_correo")
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
                solicitudEstado = it.getString(
                    it.getColumnIndexOrThrow("solicitud_estado")
                ),
                movimientoId = movimientoId,
                fechaEntrada = fechaEntrada
            )
        }
    }

    fun registrarEntrada(
        vehiculoId: Int
    ): ResultadoMovimiento {

        val db = dbHelper.writableDatabase
        db.beginTransaction()

        val resultado = try {

            when {
                !solicitudEstaAprobada(db, vehiculoId) -> {
                    ResultadoMovimiento(
                        exito = false,
                        mensaje = "El vehículo no tiene una solicitud aprobada."
                    )
                }

                obtenerMovimientoActivoId(
                    db,
                    vehiculoId
                ) != null -> {
                    ResultadoMovimiento(
                        exito = false,
                        mensaje = "El vehículo ya se encuentra dentro."
                    )
                }

                else -> {
                    val valores = ContentValues().apply {
                        put("vehiculo_id", vehiculoId)
                        put("estado", "DENTRO")
                    }

                    val idMovimiento = db.insert(
                        AppDatabaseHelper.TABLA_MOVIMIENTOS,
                        null,
                        valores
                    )

                    if (idMovimiento == -1L) {
                        ResultadoMovimiento(
                            exito = false,
                            mensaje = "No se pudo registrar la entrada."
                        )
                    } else {
                        db.setTransactionSuccessful()

                        ResultadoMovimiento(
                            exito = true,
                            mensaje = "Entrada registrada correctamente."
                        )
                    }
                }
            }

        } catch (e: Exception) {
            ResultadoMovimiento(
                exito = false,
                mensaje = "Error al registrar entrada: ${e.message}"
            )
        } finally {
            db.endTransaction()
        }

        return resultado
    }

    fun registrarSalida(
        vehiculoId: Int
    ): ResultadoMovimiento {

        val db = dbHelper.writableDatabase
        db.beginTransaction()

        val resultado = try {

            val movimientoId =
                obtenerMovimientoActivoId(
                    db,
                    vehiculoId
                )

            if (movimientoId == null) {

                ResultadoMovimiento(
                    exito = false,
                    mensaje = "El vehículo no tiene una entrada activa."
                )

            } else {

                val sentencia = db.compileStatement(
                    """
                    UPDATE movimientos
                    SET
                        fecha_salida = CURRENT_TIMESTAMP,
                        estado = 'FINALIZADO'
                    WHERE id = ?
                    AND estado = 'DENTRO'
                    """.trimIndent()
                )

                sentencia.bindLong(
                    1,
                    movimientoId.toLong()
                )

                val filasActualizadas =
                    sentencia.executeUpdateDelete()

                if (filasActualizadas > 0) {
                    db.setTransactionSuccessful()

                    ResultadoMovimiento(
                        exito = true,
                        mensaje = "Salida registrada correctamente."
                    )
                } else {
                    ResultadoMovimiento(
                        exito = false,
                        mensaje = "No se pudo registrar la salida."
                    )
                }
            }

        } catch (e: Exception) {
            ResultadoMovimiento(
                exito = false,
                mensaje = "Error al registrar salida: ${e.message}"
            )
        } finally {
            db.endTransaction()
        }

        return resultado
    }

    private fun solicitudEstaAprobada(
        db: SQLiteDatabase,
        vehiculoId: Int
    ): Boolean {

        val consulta = """
            SELECT 1
            FROM solicitudes
            WHERE vehiculo_id = ?
            AND estado = 'APROBADO'
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(
            consulta,
            arrayOf(vehiculoId.toString())
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    private fun obtenerMovimientoActivoId(
        db: SQLiteDatabase,
        vehiculoId: Int
    ): Int? {

        val consulta = """
            SELECT id
            FROM movimientos
            WHERE vehiculo_id = ?
            AND estado = 'DENTRO'
            ORDER BY id DESC
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(
            consulta,
            arrayOf(vehiculoId.toString())
        )

        cursor.use {
            if (!it.moveToFirst()) {
                return null
            }

            return it.getInt(
                it.getColumnIndexOrThrow("id")
            )
        }
    }
}

data class ResultadoMovimiento(
    val exito: Boolean,
    val mensaje: String
)