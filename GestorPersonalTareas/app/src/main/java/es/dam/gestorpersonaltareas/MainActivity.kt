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
import es.dam.gestorpersonaltareas.ui.PantallaTareasEjemplo
import es.dam.gestorpersonaltareas.ui.theme.GestorPersonalTareasTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private object Destinos {
    const val PRINCIPAL = "principal"
    const val FORMULARIO = "formulario"
    const val EJEMPLO = "ejemplo"
}

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
                var pantallaActual by rememberSaveable { mutableStateOf(Destinos.PRINCIPAL) }
                var tareaAEditarId by rememberSaveable { mutableStateOf<Long?>(null) }
                val tareaAEditar = tareas.find { it.id == tareaAEditarId }

                BackHandler(enabled = pantallaActual != Destinos.PRINCIPAL) {
                    if (pantallaActual == Destinos.FORMULARIO) {
                        tareaAEditarId = null
                    }
                    pantallaActual = Destinos.PRINCIPAL
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (pantallaActual) {
                        Destinos.FORMULARIO -> {
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
                                    tareaAEditarId = null
                                    pantallaActual = Destinos.PRINCIPAL
                                },
                                alCancelar = {
                                    tareaAEditarId = null
                                    pantallaActual = Destinos.PRINCIPAL
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        Destinos.EJEMPLO -> {
                            PantallaTareasEjemplo(
                                alVolver = {
                                    pantallaActual = Destinos.PRINCIPAL
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        else -> {
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
                                    pantallaActual = Destinos.FORMULARIO
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
                                    pantallaActual = Destinos.FORMULARIO
                                },
                                alVerTareasEjemplo = {
                                    pantallaActual = Destinos.EJEMPLO
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}