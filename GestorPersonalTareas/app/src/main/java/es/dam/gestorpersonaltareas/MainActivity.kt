package es.dam.gestorpersonaltareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import es.dam.gestorpersonaltareas.model.Prioridades
import es.dam.gestorpersonaltareas.model.Tarea
import es.dam.gestorpersonaltareas.ui.PantallaFormulario
import es.dam.gestorpersonaltareas.ui.PantallaPrincipal
import es.dam.gestorpersonaltareas.ui.theme.GestorPersonalTareasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestorPersonalTareasTheme {
                var tareas by remember {
                    mutableStateOf(
                        listOf(
                            Tarea(
                                id = 1,
                                titulo = "Estudiar Jetpack Compose",
                                prioridad = Prioridades.ALTA
                            ),
                            Tarea(
                                id = 2,
                                titulo = "Preparar emuladores",
                                prioridad = Prioridades.MEDIA,
                                completada = true
                            )
                        )
                    )
                }
                var soloPendientes by rememberSaveable { mutableStateOf(false) }
                var mostrandoFormulario by rememberSaveable { mutableStateOf(false) }
                var tareaAEditarId by rememberSaveable { mutableStateOf<Long?>(null) }
                val tareaAEditar = tareas.find { it.id == tareaAEditarId }

                BackHandler(enabled = mostrandoFormulario) {
                    mostrandoFormulario = false
                    tareaAEditarId = null
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (mostrandoFormulario) {
                        PantallaFormulario(
                            tareaInicial = tareaAEditar,
                            alGuardar = { titulo, descripcion, prioridad ->
                                val actual = tareaAEditar
                                tareas = if (actual == null) {
                                    val nuevoId = (tareas.maxOfOrNull { it.id } ?: 0L) + 1L
                                    tareas + Tarea(
                                        id = nuevoId,
                                        titulo = titulo,
                                        descripcion = descripcion,
                                        prioridad = prioridad
                                    )
                                } else {
                                    tareas.map { tarea ->
                                        if (tarea.id == actual.id) {
                                            tarea.copy(
                                                titulo = titulo,
                                                descripcion = descripcion,
                                                prioridad = prioridad
                                            )
                                        } else {
                                            tarea
                                        }
                                    }
                                }
                                mostrandoFormulario = false
                                tareaAEditarId = null
                            },
                            alCancelar = {
                                mostrandoFormulario = false
                                tareaAEditarId = null
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        PantallaPrincipal(
                            tareas = tareas,
                            soloPendientes = soloPendientes,
                            alCambiarFiltro = { nuevoValor -> soloPendientes = nuevoValor },
                            alCambiarCompletada = { tareaPulsada, marcada ->
                                tareas = tareas.map { tareaActual ->
                                    if (tareaActual.id == tareaPulsada.id) {
                                        tareaActual.copy(completada = marcada)
                                    } else {
                                        tareaActual
                                    }
                                }
                            },
                            alEditar = { tarea ->
                                tareaAEditarId = tarea.id
                                mostrandoFormulario = true
                            },
                            alEliminar = { tareaBorrada ->
                                tareas = tareas.filter { it.id != tareaBorrada.id }
                            },
                            alNuevaTarea = {
                                tareaAEditarId = null
                                mostrandoFormulario = true
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}