package pe.edu.idat.idatparking.entity

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val rol: String
) {
    companion object {
        const val ROL_ALUMNO = "ALUMNO"
        const val ROL_DOCENTE = "DOCENTE"
        const val ROL_SUPERVISOR = "SUPERVISOR"
        const val ROL_SEGURIDAD = "SEGURIDAD"
    }
}