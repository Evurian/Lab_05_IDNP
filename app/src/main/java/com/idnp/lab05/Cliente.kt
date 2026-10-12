package com.idnp.lab05

/**
 * Modelo de datos que se registra en el formulario.
 * Representa a un cliente del proyecto final.
 */
data class Cliente(
    val nombre: String,
    val dni: String,
    val telefono: String,
    val correo: String
)