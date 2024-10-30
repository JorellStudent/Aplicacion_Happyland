package com.example.aplicacion_happyland.models

data class Card(
    val cardNumber: String = "",   // Número de tarjeta
    val userId: String = "",       // ID del usuario (opcional)
    val timestamp: Long = System.currentTimeMillis()  // Fecha y hora de creación
)