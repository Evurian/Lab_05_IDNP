package com.idnp.lab05

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idnp.lab05.ui.theme.Lab05Theme

private const val TAG = "Lab05"

/**
 * Pantalla con estado (stateful).
 * Aquí vive el estado de cada campo y la lógica de los eventos.
 * El formulario visual (RegistroClienteForm) solo recibe datos y eventos.
 */
@Composable
fun RegistroClienteScreen(modifier: Modifier = Modifier) {
    // Estado independiente para cada campo
    var nombre by rememberSaveable { mutableStateOf("") }
    var dni by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }

    // Un campo "tocado" ya fue editado: recién ahí se muestra su error
    var nombreTocado by rememberSaveable { mutableStateOf(false) }
    var dniTocado by rememberSaveable { mutableStateOf(false) }
    var telefonoTocado by rememberSaveable { mutableStateOf(false) }
    var correoTocado by rememberSaveable { mutableStateOf(false) }

    var mensaje by rememberSaveable { mutableStateOf<String?>(null) }
    // Lista en memoria (solo estado, NO persistida)
    val registros = remember { mutableStateListOf<Cliente>() }

    // Validación derivada del estado actual
    val errorNombre = Validador.validarNombre(nombre)
    val errorDni = Validador.validarDni(dni)
    val errorTelefono = Validador.validarTelefono(telefono)
    val errorCorreo = Validador.validarCorreo(correo)
    val formularioValido = listOf(errorNombre, errorDni, errorTelefono, errorCorreo)
        .all { it == null }

    val limpiarCampos: () -> Unit = {
        nombre = ""; dni = ""; telefono = ""; correo = ""
        nombreTocado = false; dniTocado = false
        telefonoTocado = false; correoTocado = false
        mensaje = null
    }

    val guardar: () -> Unit = {
        // Doble control: aunque el botón esté deshabilitado,
        // la acción nunca se ejecuta con datos inválidos.
        if (formularioValido) {
            val cliente = Cliente(nombre.trim(), dni, telefono, correo.trim())
            registros.add(cliente)
            Log.d(TAG, "Guardado: $cliente")
            limpiarCampos()
            mensaje = "Cliente ${cliente.nombre} guardado (${registros.size} en total)"
        } else {
            nombreTocado = true; dniTocado = true
            telefonoTocado = true; correoTocado = true
            Log.w(TAG, "Intento de guardado con datos inválidos")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RegistroClienteForm(
            nombre = nombre,
            onNombreChange = { nombre = it; nombreTocado = true },
            errorNombre = errorNombre.takeIf { nombreTocado },
            dni = dni,
            onDniChange = { dni = it; dniTocado = true },
            errorDni = errorDni.takeIf { dniTocado },
            telefono = telefono,
            onTelefonoChange = { telefono = it; telefonoTocado = true },
            errorTelefono = errorTelefono.takeIf { telefonoTocado },
            correo = correo,
            onCorreoChange = { correo = it; correoTocado = true },
            errorCorreo = errorCorreo.takeIf { correoTocado },
            guardarHabilitado = formularioValido,
            onGuardar = guardar,
            onLimpiar = limpiarCampos
        )

        mensaje?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(text = it, modifier = Modifier.padding(16.dp))
            }
        }

        if (registros.isNotEmpty()) {
            Text("Registros en memoria", style = MaterialTheme.typography.titleSmall)
            registros.forEach { c ->
                Text(
                    "• ${c.nombre} - DNI ${c.dni} - ${c.telefono}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Formulario sin estado (stateless): recibe valores, errores y eventos.
 * Se puede reutilizar en el proyecto final y probar en Preview.
 */
@Composable
fun RegistroClienteForm(
    nombre: String, onNombreChange: (String) -> Unit, errorNombre: String?,
    dni: String, onDniChange: (String) -> Unit, errorDni: String?,
    telefono: String, onTelefonoChange: (String) -> Unit, errorTelefono: String?,
    correo: String, onCorreoChange: (String) -> Unit, errorCorreo: String?,
    guardarHabilitado: Boolean,
    onGuardar: () -> Unit,
    onLimpiar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Column: organiza los elementos en vertical
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Registro de cliente", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Los campos marcados con * son obligatorios",
            style = MaterialTheme.typography.bodySmall
        )

        CampoTexto(
            valor = nombre, onCambio = onNombreChange,
            etiqueta = "Nombres y apellidos *", error = errorNombre,
            modifier = Modifier.fillMaxWidth()
        )

        // Row: DNI y celular lado a lado (datos cortos y relacionados)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CampoTexto(
                valor = dni, onCambio = onDniChange,
                etiqueta = "DNI *", error = errorDni,
                modifier = Modifier.weight(1f),
                teclado = KeyboardType.Number, maxLength = 8
            )
            CampoTexto(
                valor = telefono, onCambio = onTelefonoChange,
                etiqueta = "Celular *", error = errorTelefono,
                modifier = Modifier.weight(1f),
                teclado = KeyboardType.Phone, maxLength = 9
            )
        }

        CampoTexto(
            valor = correo, onCambio = onCorreoChange,
            etiqueta = "Correo electrónico *", error = errorCorreo,
            modifier = Modifier.fillMaxWidth(),
            teclado = KeyboardType.Email
        )

        // Row: botones de acción en horizontal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onLimpiar, modifier = Modifier.weight(1f)) {
                Text("Limpiar")
            }
            Button(
                onClick = onGuardar,
                enabled = guardarHabilitado,
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}

/** Campo de texto reutilizable con mensaje de error visible. */
@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    teclado: KeyboardType = KeyboardType.Text,
    maxLength: Int? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = { nuevo ->
            if (maxLength == null || nuevo.length <= maxLength) onCambio(nuevo)
        },
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistroClientePreview() {
    Lab05Theme { RegistroClienteScreen() }
}