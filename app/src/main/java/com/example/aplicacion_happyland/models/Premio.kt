package com.example.aplicacion_happyland.models

data class Premio(
    val nombre: String = "",
    val descripcion: String = "",
    val imagen: String = "",
    val ticketsRequeridos: Int = 0,
    val stock: Int = 0,
    var id: String = "" // Campo para guardar el ID del documento
)
