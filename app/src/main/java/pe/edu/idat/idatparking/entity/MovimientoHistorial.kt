package pe.edu.idat.idatparking.entity

data class MovimientoHistorial(
    val movimientoId: Int,
    val placa: String,
    val nombreUsuario: String,
    val fechaEntrada: String,
    val fechaSalida: String?,
    val estado: String
)