package com.idnp.lab05

/**
 * Reglas de validación del formulario.
 * Cada función devuelve null si el valor es válido
 * o un mensaje de error si no lo es.
 * No depende de Android, por eso se puede probar con JUnit.
 */
object Validador {

    private val regexCorreo = Regex("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$")

    // Regla 1: obligatorio + longitud mínima + solo letras
    fun validarNombre(valor: String): String? = when {
        valor.isBlank() -> "El nombre es obligatorio"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        !valor.all { it.isLetter() || it.isWhitespace() } -> "Solo se permiten letras"
        else -> null
    }

    // Regla 2: obligatorio + formato numérico + longitud exacta
    fun validarDni(valor: String): String? = when {
        valor.isBlank() -> "El DNI es obligatorio"
        !valor.all { it.isDigit() } -> "Solo se permiten números"
        valor.length != 8 -> "Debe tener 8 dígitos"
        else -> null
    }

    // Regla 3: celular peruano (9 dígitos, inicia con 9)
    fun validarTelefono(valor: String): String? = when {
        valor.isBlank() -> "El celular es obligatorio"
        !valor.all { it.isDigit() } -> "Solo se permiten números"
        valor.length != 9 -> "Debe tener 9 dígitos"
        !valor.startsWith("9") -> "Debe iniciar con 9"
        else -> null
    }

    // Regla 4: formato de correo electrónico
    fun validarCorreo(valor: String): String? = when {
        valor.isBlank() -> "El correo es obligatorio"
        !regexCorreo.matches(valor.trim()) -> "Formato de correo inválido"
        else -> null
    }
}