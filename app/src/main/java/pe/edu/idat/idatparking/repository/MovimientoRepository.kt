package pe.edu.idat.idatparking.repository

import android.content.Context
import pe.edu.idat.idatparking.data.AppDatabaseHelper
import pe.edu.idat.idatparking.entity.EstadisticaEstacionamiento
import pe.edu.idat.idatparking.entity.MovimientoHistorial

class MovimientoRepository(context: Context) {

    private val dbHelper =
        AppDatabaseHelper(context.applicationContext)

    companion object {
        const val CAPACIDAD_TOTAL = 20
    }

    fun obtenerEstadistica(): EstadisticaEstacionamiento {

        val ocupados = contarVehiculosDentro()

        return EstadisticaEstacionamiento(
            capacidadTotal = CAPACIDAD_TOTAL,
            ocupados = ocupados,
            disponibles = (
                    CAPACIDAD_TOTAL - ocupados
                    ).coerceAtLeast(0)
        )
    }

    fun contarVehiculosDentro(): Int {

        val db = dbHelper.readableDatabase

        val consulta = """
            SELECT COUNT(*) AS total
            FROM movimientos
            WHERE estado = 'DENTRO'
        """.trimIndent()

        val cursor = db.rawQuery(
            consulta,
            null
        )

        cursor.use {
            if (!it.moveToFirst()) {
                return 0
            }

            return it.getInt(
                it.getColumnIndexOrThrow("total")
            )
        }
    }

    fun listarHistorial(): List<MovimientoHistorial> {

        val lista =
            mutableListOf<MovimientoHistorial>()

        val db = dbHelper.readableDatabase

        val consulta = """
            SELECT
                m.id AS movimiento_id,
                v.placa,
                u.nombre AS usuario_nombre,
                m.fecha_entrada,
                m.fecha_salida,
                m.estado
            FROM movimientos m
            INNER JOIN vehiculos v
                ON v.id = m.vehiculo_id
            INNER JOIN usuarios u
                ON u.id = v.usuario_id
            ORDER BY m.id DESC
        """.trimIndent()

        val cursor = db.rawQuery(
            consulta,
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                val indiceFechaSalida =
                    it.getColumnIndexOrThrow(
                        "fecha_salida"
                    )

                val fechaSalida =
                    if (it.isNull(indiceFechaSalida)) {
                        null
                    } else {
                        it.getString(indiceFechaSalida)
                    }

                lista.add(
                    MovimientoHistorial(
                        movimientoId = it.getInt(
                            it.getColumnIndexOrThrow(
                                "movimiento_id"
                            )
                        ),
                        placa = it.getString(
                            it.getColumnIndexOrThrow(
                                "placa"
                            )
                        ),
                        nombreUsuario = it.getString(
                            it.getColumnIndexOrThrow(
                                "usuario_nombre"
                            )
                        ),
                        fechaEntrada = it.getString(
                            it.getColumnIndexOrThrow(
                                "fecha_entrada"
                            )
                        ),
                        fechaSalida = fechaSalida,
                        estado = it.getString(
                            it.getColumnIndexOrThrow(
                                "estado"
                            )
                        )
                    )
                )
            }
        }

        return lista
    }
}