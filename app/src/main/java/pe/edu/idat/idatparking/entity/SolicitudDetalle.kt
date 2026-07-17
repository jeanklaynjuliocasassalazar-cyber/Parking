package pe.edu.idat.idatparking.entity

data class SolicitudDetalle(
    val solicitudId: Int,
    val vehiculoId: Int,
    val placa: String,
    val marca: String,
    val color: String,
    val tipo: String,
    val estado: String,
    val fechaSolicitud: String
)