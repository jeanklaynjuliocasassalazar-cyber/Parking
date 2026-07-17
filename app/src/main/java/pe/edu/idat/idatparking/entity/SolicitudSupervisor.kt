package pe.edu.idat.idatparking.entity

data class SolicitudSupervisor(
    val solicitudId: Int,
    val usuarioId: Int,
    val nombreUsuario: String,
    val correoUsuario: String,
    val vehiculoId: Int,
    val placa: String,
    val marca: String,
    val color: String,
    val tipo: String,
    val estado: String,
    val fechaSolicitud: String
)