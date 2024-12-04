package com.example.aplicacion_happyland.models

data class Premio(
    val nombre: String = "", // Nombre del premio
    val descripcion: String = "", // Breve descripción del premio
    val imagen: String = "", // URL de la imagen del premio
    val ticketsRequeridos: Int = 0, // Número de tickets necesarios para canjear
    val stock: Int = 0, // Stock disponible del premio
    var id: String = "" // ID único asignado por Firestore
)

