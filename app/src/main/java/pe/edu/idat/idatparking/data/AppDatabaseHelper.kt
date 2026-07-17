package pe.edu.idat.idatparking.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "idat_parking.db"
        private const val DATABASE_VERSION = 1

        const val TABLA_USUARIOS = "usuarios"
        const val TABLA_VEHICULOS = "vehiculos"
        const val TABLA_SOLICITUDES = "solicitudes"
        const val TABLA_MOVIMIENTOS = "movimientos"

        const val USUARIO_ID = "id"
        const val USUARIO_NOMBRE = "nombre"
        const val USUARIO_CORREO = "correo"
        const val USUARIO_PASSWORD = "password"
        const val USUARIO_ROL = "rol"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)

        // Activa el control de claves foráneas en SQLite.
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        crearTablaUsuarios(db)
        crearTablaVehiculos(db)
        crearTablaSolicitudes(db)
        crearTablaMovimientos(db)
        insertarUsuariosIniciales(db)
    }

    private fun crearTablaUsuarios(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLA_USUARIOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                rol TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(sql)
    }

    private fun crearTablaVehiculos(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLA_VEHICULOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL UNIQUE,
                placa TEXT NOT NULL UNIQUE,
                marca TEXT NOT NULL,
                color TEXT NOT NULL,
                tipo TEXT NOT NULL,
                FOREIGN KEY (usuario_id)
                    REFERENCES $TABLA_USUARIOS(id)
                    ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(sql)
    }

    private fun crearTablaSolicitudes(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLA_SOLICITUDES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                vehiculo_id INTEGER NOT NULL UNIQUE,
                estado TEXT NOT NULL DEFAULT 'PENDIENTE',
                fecha_solicitud TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (usuario_id)
                    REFERENCES $TABLA_USUARIOS(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (vehiculo_id)
                    REFERENCES $TABLA_VEHICULOS(id)
                    ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(sql)
    }

    private fun crearTablaMovimientos(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLA_MOVIMIENTOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                vehiculo_id INTEGER NOT NULL,
                fecha_entrada TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                fecha_salida TEXT,
                estado TEXT NOT NULL DEFAULT 'DENTRO',
                FOREIGN KEY (vehiculo_id)
                    REFERENCES $TABLA_VEHICULOS(id)
                    ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(sql)
    }

    private fun insertarUsuariosIniciales(db: SQLiteDatabase) {
        insertarUsuario(
            db = db,
            nombre = "Luis Ramos Paredes",
            correo = "a76543210@idat.pe",
            password = "1234",
            rol = "ALUMNO"
        )

        insertarUsuario(
            db = db,
            nombre = "Ana López Ramírez",
            correo = "d72345678@idat.pe",
            password = "1234",
            rol = "DOCENTE"
        )

        insertarUsuario(
            db = db,
            nombre = "Carlos Mendoza Torres",
            correo = "s74521890@idat.pe",
            password = "1234",
            rol = "SUPERVISOR"
        )

        insertarUsuario(
            db = db,
            nombre = "Pedro Huamán Condori",
            correo = "seguridad01@idat.pe",
            password = "1234",
            rol = "SEGURIDAD"
        )
    }

    private fun insertarUsuario(
        db: SQLiteDatabase,
        nombre: String,
        correo: String,
        password: String,
        rol: String
    ) {
        val valores = ContentValues().apply {
            put(USUARIO_NOMBRE, nombre)
            put(USUARIO_CORREO, correo)
            put(USUARIO_PASSWORD, password)
            put(USUARIO_ROL, rol)
        }

        db.insertOrThrow(
            TABLA_USUARIOS,
            null,
            valores
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_MOVIMIENTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_SOLICITUDES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_VEHICULOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIOS")

        onCreate(db)
    }
}