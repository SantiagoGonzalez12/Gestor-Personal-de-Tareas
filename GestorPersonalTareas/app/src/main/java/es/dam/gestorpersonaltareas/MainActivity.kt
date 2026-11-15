package es.dam.gestorpersonaltareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import es.dam.gestorpersonaltareas.model.Prioridades
import es.dam.gestorpersonaltareas.model.Tarea
import es.dam.gestorpersonaltareas.ui.PantallaPrincipal
import es.dam.gestorpersonaltareas.ui.theme.GestorPersonalTareasTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestorPersonalTareasTheme {
                var tareas by remember {
                    mutableStateOf(
                        listOf(
                            Tarea(id = 1, titulo = "Estudiar Jetpack Compose", prioridad = Prioridades.ALTA),
                            Tarea(id = 2, titulo = "Preparar emuladores", prioridad = Prioridades.MEDIA, completada = true)
                        )
                    )
                }
                var soloPendientes by rememberSaveable { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                        alEditar = { },
                        alEliminar = { tareaBorrada ->
                            tareas = tareas.filter { it.id != tareaBorrada.id }
                        },
                        alNuevaTarea = { },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}