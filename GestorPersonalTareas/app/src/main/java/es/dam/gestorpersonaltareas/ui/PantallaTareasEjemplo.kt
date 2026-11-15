package es.dam.gestorpersonaltareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.gson.JsonParseException
import es.dam.gestorpersonaltareas.R
import es.dam.gestorpersonaltareas.network.ApiTareas
import es.dam.gestorpersonaltareas.network.ServicioTareas
import es.dam.gestorpersonaltareas.network.TareaRemota
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface EstadoCarga {
    data object Inicial : EstadoCarga
    data object Cargando : EstadoCarga
    data class Datos(val tareas: List<TareaRemota>) : EstadoCarga
    data class Error(val mensaje: String) : EstadoCarga
}

suspend fun descargarTareasEjemplo(
    api: ApiTareas,
    msgErrorHttp: String,
    msgErrorJson: String,
    msgErrorRed: String
): EstadoCarga = try {
    EstadoCarga.Datos(api.listar(usuario = 1))
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    EstadoCarga.Error("$msgErrorHttp (${e.code()})")
} catch (e: JsonParseException) {
    EstadoCarga.Error(msgErrorJson)
} catch (e: IOException) {
    EstadoCarga.Error(msgErrorRed)
}

@Composable
fun PantallaTareasEjemplo(
    alVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var estado by remember { mutableStateOf<EstadoCarga>(EstadoCarga.Inicial) }
    val scope = rememberCoroutineScope()

    val msgErrorHttp = stringResource(R.string.error_http)
    val msgErrorJson = stringResource(R.string.error_json)
    val msgErrorRed = stringResource(R.string.error_red)

    fun solicitarTareas() {
        if (estado is EstadoCarga.Cargando) return
        estado = EstadoCarga.Cargando
        scope.launch {
            estado = descargarTareasEjemplo(
                api = ServicioTareas.api,
                msgErrorHttp = msgErrorHttp,
                msgErrorJson = msgErrorJson,
                msgErrorRed = msgErrorRed
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.titulo_ejemplo),
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = stringResource(R.string.subtitulo_ejemplo),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { solicitarTareas() },
                enabled = estado !is EstadoCarga.Cargando,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.cargar_tareas_ejemplo))
            }

            OutlinedButton(
                onClick = alVolver,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.volver))
            }
        }

        when (val actual = estado) {
            is EstadoCarga.Inicial -> {
                Text(
                    text = stringResource(R.string.ejemplo_inicial),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            is EstadoCarga.Cargando -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is EstadoCarga.Datos -> {
                if (actual.tareas.isEmpty()) {
                    Text(
                        text = stringResource(R.string.ejemplo_vacio),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(actual.tareas, key = { it.id }) { item ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = stringResource(
                                            R.string.item_tarea_ejemplo,
                                            item.id,
                                            item.title
                                        ),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = if (item.completed) {
                                            stringResource(R.string.estado_completada)
                                        } else {
                                            stringResource(R.string.estado_pendiente)
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is EstadoCarga.Error -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = actual.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = { solicitarTareas() }) {
                        Text(text = stringResource(R.string.reintentar))
                    }
                }
            }
        }
    }
}