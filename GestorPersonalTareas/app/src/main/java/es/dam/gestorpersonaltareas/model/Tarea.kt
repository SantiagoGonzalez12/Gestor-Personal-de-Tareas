package es.dam.gestorpersonaltareas.model

import androidx.room.Entity
import androidx.room.PrimaryKey
object Prioridades {
    const val BAJA = "Baja"
    const val MEDIA = "Media"
    const val ALTA = "Alta"
    val todas = listOf(BAJA, MEDIA, ALTA)
}

@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descripcion: String = "",
    val prioridad: String = Prioridades.MEDIA,
    val completada: Boolean = false
)