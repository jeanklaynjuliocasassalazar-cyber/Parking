package pe.edu.idat.idatparking.entity

data class VehiculoSeguridad(
    val vehiculoId: Int,
    val usuarioId: Int,
    val nombreUsuario: String,
    val correoUsuario: String,
    val placa: String,
    val marca: String,
    val color: String,
    val tipo: String,
    val solicitudEstado: String,
    val movimientoId: Int?,
    val fechaEntrada: String?
) {
    val estaDentro: Boolean
        get() = movimientoId != null
}