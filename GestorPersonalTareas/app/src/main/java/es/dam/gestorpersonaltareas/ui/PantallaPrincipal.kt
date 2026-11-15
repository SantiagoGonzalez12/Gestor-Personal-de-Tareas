package es.dam.gestorpersonaltareas.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.dam.gestorpersonaltareas.model.Tarea
import es.dam.gestorpersonaltareas.R
import androidx.compose.material.icons.filled.Add

@Composable
fun TarjetaTarea(
    tarea: Tarea,
    alCambiarCompletada: (Boolean) -> Unit,
    alPedirEditar: () -> Unit,
    alPedirBorrar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = alCambiarCompletada
            )
            Text(
                text = tarea.titulo,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
            Text(
                text = stringResource(R.string.etiqueta_prioridad, tarea.prioridad),
                style = MaterialTheme.typography.bodySmall
            )
            IconButton(onClick = alPedirEditar) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.editar)
                )
            }
            IconButton(onClick = alPedirBorrar) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.eliminar)
                )
            }
        }
    }
}

@Composable
fun PantallaPrincipal(
    tareas: List<Tarea>,
    soloPendientes: Boolean,
    alCambiarFiltro: (Boolean) -> Unit,
    alCambiarCompletada: (Tarea, Boolean) -> Unit,
    alEditar: (Tarea) -> Unit,
    alEliminar: (Tarea) -> Unit,
    alNuevaTarea: () -> Unit,
    alVerTareasEjemplo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tareaAEliminar by remember { mutableStateOf<Tarea?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.titulo_principal),
            style = MaterialTheme.typography.headlineSmall
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.filtro_pendientes),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = soloPendientes,
                onCheckedChange = alCambiarFiltro
            )
        }

        Button(
            onClick = alNuevaTarea,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.aniadir_tarea))
        }

        OutlinedButton(
            onClick = alVerTareasEjemplo,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.ver_tareas_ejemplo))
        }

        val tareasVisibles = if (soloPendientes) {
            tareas.filter { !it.completada }
        } else {
            tareas
        }

        if (tareasVisibles.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_tarea),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (tareas.isEmpty()) {
                        stringResource(R.string.sin_tareas)
                    } else {
                        stringResource(R.string.tareas_vacias)
                    }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareasVisibles, key = { it.id }) { tarea ->
                    TarjetaTarea(
                        tarea = tarea,
                        alCambiarCompletada = { marcado -> alCambiarCompletada(tarea, marcado) },
                        alPedirEditar = { alEditar(tarea) },
                        alPedirBorrar = { tareaAEliminar = tarea }
                    )
                }
            }
        }

        val pendiente = tareaAEliminar
        if (pendiente != null) {
            AlertDialog(
                onDismissRequest = { tareaAEliminar = null },
                title = { Text(stringResource(R.string.titulo_confirmacion)) },
                text = { Text(stringResource(R.string.mensaje_confirmacion, pendiente.titulo)) },
                confirmButton = {
                    TextButton(onClick = {
                        alEliminar(pendiente)
                        tareaAEliminar = null
                    }) {
                        Text(stringResource(R.string.confirmar))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { tareaAEliminar = null }) {
                        Text(stringResource(R.string.cancelar))
                    }
                }
            )
        }
    }
}