package com.example.aplicacion_happyland.models

data class Card(
    val idTarjeta: String = "",       // ID de la tarjeta (automático o manual)
    val usuarioId: String = "",       // ID del usuario vinculado a la tarjeta
    val codigo: String = "",          // Número de tarjeta (ej. "0200-0000-1247-2960")
    val saldoEfectivo: Double = 0.0,  // Saldo en dinero real
    val saldoBonus: Double = 0.0,     // Saldo en bonos
    val tickets: Int = 0,             // Cantidad de tickets
    val estado: String = "Activo",    // Estado de la tarjeta ("Activo" o "Inactivo")
    val fechaCreacion: Long = System.currentTimeMillis(), // Fecha de creación (timestamp)
    val ultimaModificacion: Long = System.currentTimeMillis() // Última modificación (timestamp)
)
