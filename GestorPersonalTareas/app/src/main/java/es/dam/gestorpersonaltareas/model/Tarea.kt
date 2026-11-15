package es.dam.gestorpersonaltareas.model

object Prioridades {
    const val BAJA = "Baja"
    const val MEDIA = "Media"
    const val ALTA = "Alta"
    val todas = listOf(BAJA, MEDIA, ALTA)
}

data class Tarea(
    val id: Long = 0,
    val titulo: String,
    val descripcion: String = "",
    val prioridad: String = Prioridades.MEDIA,
    val completada: Boolean = false
)