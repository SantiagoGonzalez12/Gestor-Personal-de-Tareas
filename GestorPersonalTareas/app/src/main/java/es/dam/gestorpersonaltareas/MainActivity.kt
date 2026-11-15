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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.dam.gestorpersonaltareas.data.AppDatabase
import es.dam.gestorpersonaltareas.model.Tarea
import es.dam.gestorpersonaltareas.preferences.guardarFiltro
import es.dam.gestorpersonaltareas.preferences.observarFiltro
import es.dam.gestorpersonaltareas.ui.PantallaFormulario
import es.dam.gestorpersonaltareas.ui.PantallaPrincipal
import es.dam.gestorpersonaltareas.ui.theme.GestorPersonalTareasTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestorPersonalTareasTheme {
                val dao = remember { AppDatabase.obtener(applicationContext).tareaDao() }
                val flujoTareas = remember(dao) { dao.observar() }
                val tareas by flujoTareas.collectAsStateWithLifecycle(initialValue = emptyList())

                val flujoFiltro = remember { observarFiltro(applicationContext) }
                val soloPendientes by flujoFiltro.collectAsStateWithLifecycle(initialValue = false)

                val scope = rememberCoroutineScope()
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
                                if (actual == null) {
                                    dao.insertar(
                                        Tarea(
                                            titulo = titulo,
                                            descripcion = descripcion,
                                            prioridad = prioridad
                                        )
                                    )
                                } else {
                                    dao.actualizar(
                                        actual.copy(
                                            titulo = titulo,
                                            descripcion = descripcion,
                                            prioridad = prioridad
                                        )
                                    )
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
                            alCambiarFiltro = { nuevoValor ->
                                scope.launch {
                                    try {
                                        guardarFiltro(applicationContext, nuevoValor)
                                    } catch (e: CancellationException) {
                                        throw e
                                    } catch (_: Exception) {
                                    }
                                }
                            },
                            alCambiarCompletada = { tareaPulsada, marcada ->
                                scope.launch {
                                    try {
                                        dao.actualizar(tareaPulsada.copy(completada = marcada))
                                    } catch (e: CancellationException) {
                                        throw e
                                    } catch (_: Exception) {
                                    }
                                }
                            },
                            alEditar = { tarea ->
                                tareaAEditarId = tarea.id
                                mostrandoFormulario = true
                            },
                            alEliminar = { tareaBorrada ->
                                scope.launch {
                                    try {
                                        dao.eliminar(tareaBorrada)
                                    } catch (e: CancellationException) {
                                        throw e
                                    } catch (_: Exception) {
                                    }
                                }
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