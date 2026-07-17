package pe.edu.idat.idatparking.repository

import android.content.ContentValues
import android.content.Context
import pe.edu.idat.idatparking.data.AppDatabaseHelper
import pe.edu.idat.idatparking.entity.SolicitudSupervisor

class SolicitudRepository(context: Context) {

    private val dbHelper =
        AppDatabaseHelper(context.applicationContext)

    fun listarSolicitudesPendientes(): List<SolicitudSupervisor> {

        val lista = mutableListOf<SolicitudSupervisor>()
        val db = dbHelper.readableDatabase

        val consulta = """
            SELECT
                s.id AS solicitud_id,
                s.usuario_id,
                u.nombre AS usuario_nombre,
                u.correo AS usuario_correo,
                v.id AS vehiculo_id,
                v.placa,
                v.marca,
                v.color,
                v.tipo,
                s.estado,
                s.fecha_solicitud
            FROM solicitudes s
            INNER JOIN usuarios u
                ON u.id = s.usuario_id
            INNER JOIN vehiculos v
                ON v.id = s.vehiculo_id
            WHERE s.estado = ?
            ORDER BY s.id DESC
        """.trimIndent()

        val cursor = db.rawQuery(
            consulta,
            arrayOf("PENDIENTE")
        )

        cursor.use {
            while (it.moveToNext()) {

                val solicitud = SolicitudSupervisor(
                    solicitudId = it.getInt(
                        it.getColumnIndexOrThrow(
                            "solicitud_id"
                        )
                    ),
                    usuarioId = it.getInt(
                        it.getColumnIndexOrThrow(
                            "usuario_id"
                        )
                    ),
                    nombreUsuario = it.getString(
                        it.getColumnIndexOrThrow(
                            "usuario_nombre"
                        )
                    ),
                    correoUsuario = it.getString(
                        it.getColumnIndexOrThrow(
                            "usuario_correo"
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
                    )
                )

                lista.add(solicitud)
            }
        }

        return lista
    }

    fun actualizarEstado(
        solicitudId: Int,
        nuevoEstado: String
    ): Boolean {

        if (
            nuevoEstado != "APROBADO" &&
            nuevoEstado != "RECHAZADO"
        ) {
            return false
        }

        val db = dbHelper.writableDatabase

        val valores = ContentValues().apply {
            put("estado", nuevoEstado)
        }

        val filasActualizadas = db.update(
            AppDatabaseHelper.TABLA_SOLICITUDES,
            valores,
            "id = ? AND estado = ?",
            arrayOf(
                solicitudId.toString(),
                "PENDIENTE"
            )
        )

        return filasActualizadas > 0
    }
}