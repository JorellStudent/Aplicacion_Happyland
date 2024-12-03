package com.example.aplicacion_happyland.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.aplicacion_happyland.R
import com.example.aplicacion_happyland.models.Premio
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PrizesScreen(navController: NavController, cardNumber: String, ticketsIniciales: Int) {
    val premios = remember { mutableStateListOf<Premio>() }
    val db = FirebaseFirestore.getInstance()
    var ticketsDisponibles by remember { mutableIntStateOf(ticketsIniciales) }
    var selectedFilter by remember { mutableStateOf("Todos") }
    var selectedImage by remember { mutableStateOf<String?>(null) }

    // Función para cargar premios
    fun cargarPremios() {
        db.collection("premios")
            .get()
            .addOnSuccessListener { documents ->
                premios.clear()
                for (document in documents) {
                    val premio = document.toObject(Premio::class.java).apply {
                        id = document.id
                    }
                    premios.add(premio)
                }
            }
            .addOnFailureListener { e ->
                Log.e("PrizesScreen", "Error al cargar premios: ${e.message}")
                Toast.makeText(navController.context, "Error al cargar premios", Toast.LENGTH_SHORT).show()
            }
    }

    // Cargar premios al inicio
    LaunchedEffect(Unit) {
        cargarPremios()
    }

    // Filtrar premios
    val filteredPremios = when (selectedFilter) {
        "A-Z" -> premios.sortedBy { it.nombre }
        "Tickets Asc" -> premios.sortedBy { it.ticketsRequeridos }
        "Tickets Desc" -> premios.sortedByDescending { it.ticketsRequeridos }
        else -> premios
    }

    // UI de la pantalla
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondocolor),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Premios Disponibles",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Tickets disponibles: $ticketsDisponibles",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                FilterButton("A-Z") { selectedFilter = "A-Z" }
                FilterButton("Tickets ↑") { selectedFilter = "Tickets Asc" }
                FilterButton("Tickets ↓") { selectedFilter = "Tickets Desc" }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredPremios.isEmpty()) {
                Text(
                    text = "No hay premios disponibles.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                filteredPremios.forEach { premio ->
                    PremioCard(
                        premio = premio,
                        ticketsDisponibles = ticketsDisponibles,
                        onCanjearClick = {
                            canjearPremio(
                                db = db,
                                premio = premio,
                                cardNumber = cardNumber,
                                onSuccess = {
                                    ticketsDisponibles -= premio.ticketsRequeridos
                                    Toast.makeText(
                                        navController.context,
                                        "Premio canjeado exitosamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onFailure = { error ->
                                    Toast.makeText(
                                        navController.context,
                                        "Error: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onUpdate = { cargarPremios() }
                            )
                        },
                        onImageClick = { selectedImage = it }
                    )
                }
            }
        }

        // Dialogo para mostrar imagen en pantalla completa
        selectedImage?.let { imageUrl ->
            Dialog(onDismissRequest = { selectedImage = null }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 600.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun PremioCard(
    premio: Premio,
    ticketsDisponibles: Int,
    onCanjearClick: () -> Unit,
    onImageClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = premio.imagen,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
                    .clickable { onImageClick(premio.imagen) }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = premio.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = premio.descripcion, fontSize = 14.sp, color = Color.Gray)
                Text("Tickets requeridos: ${premio.ticketsRequeridos}", fontSize = 14.sp, color = Color.Black)
                Text("Stock: ${premio.stock}", fontSize = 14.sp, color = if (premio.stock > 0) Color.Green else Color.Red)
            }
            Button(
                onClick = onCanjearClick,
                enabled = ticketsDisponibles >= premio.ticketsRequeridos && premio.stock > 0
            ) {
                Text("Canjear")
            }
        }
    }
}



fun canjearPremio(
    db: FirebaseFirestore,
    premio: Premio,
    cardNumber: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit,
    onUpdate: () -> Unit // Nuevo callback para actualizar la UI
) {
    val tarjetaQuery = db.collection("tarjetas").whereEqualTo("codigo", cardNumber)
    val premioRef = db.collection("premios").document(premio.id)

    tarjetaQuery.get().addOnSuccessListener { querySnapshot ->
        if (querySnapshot.documents.isNotEmpty()) {
            val tarjetaRef = querySnapshot.documents[0].reference

            db.runTransaction { transaction ->
                val tarjetaSnapshot = transaction.get(tarjetaRef)
                val premioSnapshot = transaction.get(premioRef)

                if (!premioSnapshot.exists()) {
                    throw Exception("El premio con ID '${premio.id}' no existe en la base de datos.")
                }

                val currentTickets = tarjetaSnapshot.getLong("tickets")?.toInt() ?: 0
                val currentStock = premioSnapshot.getLong("stock")?.toInt() ?: 0

                if (currentTickets < premio.ticketsRequeridos) {
                    throw Exception("Tickets insuficientes para canjear este premio.")
                }
                if (currentStock <= 0) {
                    throw Exception("Stock insuficiente para este premio.")
                }

                transaction.update(tarjetaRef, "tickets", currentTickets - premio.ticketsRequeridos)
                transaction.update(premioRef, "stock", currentStock - 1)

                val historialData = mapOf(
                    "premio" to premio.nombre,
                    "ticketsUsados" to premio.ticketsRequeridos,
                    "fechaCreacion" to Timestamp.now(),
                    "tarjetaId" to tarjetaSnapshot.id,
                    "tipoEvento" to "canje",
                    "ubicacion" to "Sede Arica",
                    "usuarioId" to tarjetaSnapshot.getString("usuarioId")
                )
                db.collection("historiales").add(historialData)
            }.addOnSuccessListener {
                Log.d("canjearPremio", "Stock actualizado correctamente.")
                onSuccess()
                onUpdate() // Llamar al callback para actualizar la UI
            }.addOnFailureListener { exception ->
                Log.e("canjearPremio", "Error en la transacción: ${exception.message}")
                onFailure("Error en la transacción: ${exception.message}")
            }
        } else {
            Log.e("canjearPremio", "No se encontró la tarjeta con el código proporcionado.")
            onFailure("No se encontró la tarjeta con el código proporcionado.")
        }
    }.addOnFailureListener { exception ->
        Log.e("canjearPremio", "Error al buscar la tarjeta: ${exception.message}")
        onFailure("Error al buscar la tarjeta: ${exception.message}")
    }
}





