package es.dam.gestorpersonaltareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.dam.gestorpersonaltareas.R
import es.dam.gestorpersonaltareas.model.Prioridades
import es.dam.gestorpersonaltareas.model.Tarea

fun validarTitulo(
    texto: String,
    mensajeVacio: String = "El título no puede estar vacío",
    mensajeCorto: String = "El título no puede ser menos de 3 caracteres"
): String? {
    val limpio = texto.trim()
    return when {
        limpio.isBlank() -> mensajeVacio
        limpio.length < 3 -> mensajeCorto
        else -> null
    }
}

fun validarPrioridad(
    prioridad: String,
    mensajeInvalida: String = "La prioridad no es válida"
): String? {
    return if (prioridad !in Prioridades.todas) {
        mensajeInvalida
    } else {
        null
    }
}

@Composable
fun SelectorPrioridad(
    prioridadActual: String,
    alSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expandido = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.etiqueta_prioridad, prioridadActual))
        }
        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            Prioridades.todas.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(text = opcion) },
                    onClick = {
                        alSeleccionar(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaFormulario(
    tareaInicial: Tarea?,
    alGuardar: (titulo: String, descripcion: String, prioridad: String) -> Unit,
    alCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var titulo by rememberSaveable { mutableStateOf(tareaInicial?.titulo ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(tareaInicial?.descripcion ?: "") }
    var prioridad by rememberSaveable {
        mutableStateOf(tareaInicial?.prioridad ?: Prioridades.MEDIA)
    }
    var intentoGuardar by rememberSaveable { mutableStateOf(false) }

    val msgTituloVacio = stringResource(R.string.form_titulo_vacio)
    val msgTituloCorto = stringResource(R.string.form_titulo_menor_3)
    val msgPrioridadInvalida = stringResource(R.string.form_prioridad_no_valida)

    val errorTitulo = validarTitulo(titulo, msgTituloVacio, msgTituloCorto)
    val errorPrioridad = validarPrioridad(prioridad, msgPrioridadInvalida)
    val formularioValido = errorTitulo == null && errorPrioridad == null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (tareaInicial == null) {
                stringResource(R.string.form_nueva_tarea)
            } else {
                stringResource(R.string.form_editar_tarea)
            },
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text(stringResource(R.string.form_titulo)) },
            singleLine = true,
            isError = intentoGuardar && errorTitulo != null,
            supportingText = {
                if (intentoGuardar && errorTitulo != null) {
                    Text(text = errorTitulo)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text(stringResource(R.string.form_descripcion)) },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        SelectorPrioridad(
            prioridadActual = prioridad,
            alSeleccionar = { prioridad = it }
        )

        if (intentoGuardar && errorPrioridad != null) {
            Text(
                text = errorPrioridad,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = alCancelar,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.cancelar))
            }
            Button(
                onClick = {
                    intentoGuardar = true
                    if (formularioValido) {
                        alGuardar(titulo.trim(), descripcion.trim(), prioridad)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.form_guardar))
            }
        }
    }
}