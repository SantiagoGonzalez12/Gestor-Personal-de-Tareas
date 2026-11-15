package es.dam.gestorpersonaltareas.network

data class TareaRemota(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)