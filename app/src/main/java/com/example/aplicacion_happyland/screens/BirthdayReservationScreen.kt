package com.example.aplicacion_happyland.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacion_happyland.R
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun BirthdayReservationScreen(navController: NavController) {
    // Instancia de Firestore y estados para gestionar datos y visualización
    val db = FirebaseFirestore.getInstance()
    val packages = remember { mutableStateListOf<Map<String, Any>>() } // Lista de paquetes de cumpleaños
    var isLoading by remember { mutableStateOf(true) } // Indicador de carga
    var showFaq by remember { mutableStateOf(false) } // Estado para mostrar/ocultar preguntas frecuentes

    // Lista de colores para personalizar las tarjetas de los paquetes
    val packageColors = listOf(
        Color(0xFFE57373), // Rojo
        Color(0xFF81C784), // Verde
        Color(0xFF64B5F6), // Azul
        Color(0xFFFFD54F), // Amarillo
        Color(0xFFBA68C8)  // Morado
    )

    // Cargar los paquetes de cumpleaños desde Firebase
    LaunchedEffect(Unit) {
        db.collection("paquetes")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    packages.clear()
                    // Ordenar los paquetes por precio descendente
                    packages.addAll(
                        documents.map { it.data }.sortedByDescending {
                            (it["precio"] as? Number)?.toDouble() ?: 0.0
                        }
                    )
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    // Contenedor principal
    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.fondocolor),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Contenido de la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Habilitar desplazamiento vertical
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título principal
            Text(
                text = "PAQUETES DE CUMPLEAÑOS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFAFAFA),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar indicador de carga o lista de paquetes
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                if (packages.isEmpty()) {
                    // Mensaje en caso de que no haya paquetes disponibles
                    Text(
                        text = "No hay paquetes disponibles",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                } else {
                    // Lista horizontal de paquetes
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(540.dp), // Altura uniforme para las tarjetas
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(packages.size) { index ->
                            val packageData = packages[index]
                            val title = packageData["nombre"].toString()
                            val price = "$${packageData["precio"].toString()}"
                            val features = buildFeaturesList(packageData) // Generar lista de características
                            val color = packageColors[index % packageColors.size] // Asignar color por índice

                            // Componente de tarjeta para cada paquete
                            BirthdayPackageCard(
                                title = title,
                                price = price,
                                features = features,
                                backgroundColor = color,
                                onClick = {
                                    // Navegar a la pantalla de reserva con el título del paquete
                                    navController.navigate("calendar/$title")
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para mostrar/ocultar preguntas frecuentes
            Button(
                onClick = { showFaq = !showFaq },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (showFaq) "Ocultar Preguntas Frecuentes" else "Preguntas Frecuentes",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar preguntas frecuentes si el estado `showFaq` es verdadero
            if (showFaq) {
                FaqSection()
            }
        }
    }
}

// Componente para una tarjeta de paquete de cumpleaños
@Composable
fun BirthdayPackageCard(
    title: String,
    price: String,
    features: List<String>,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(540.dp) // Altura uniforme para las tarjetas
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Título del paquete
        Text(
            text = title,
            fontSize = 32.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Precio del paquete
        Text(
            text = price,
            fontSize = 24.sp,
            color = Color.Yellow,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de características
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            features.forEach { feature ->
                Text(
                    text = "• $feature",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para seleccionar el paquete
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// Sección de preguntas frecuentes
@Composable
fun FaqSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Preguntas Frecuentes Cumpleaños",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = """
            ¿Qué incluye la celebración de cumpleaños en Happyland?
            • Sector exclusivo, especialmente ambientado.
            • Tarjeta Happyland con 60 o 90 minutos de juegos.
            • Snack dulce y salado + jugo individual.
            • Anfitrión exclusivo y piñata (paquete Happyland).
            • Microondas disponible.

            ¿Qué puedo llevar adicional para la celebración de cumpleaños?
            • Alimentos sin cocción, cotillón (sin dañar instalaciones).

            ¿En qué horarios se puede celebrar un cumpleaños en Happyland?
            • 11:00 a 13:00 / 14:00 a 16:00 / 17:00 a 19:00.

            ¿Cuál es el procedimiento para reservar un cumpleaños?
            • A través del sitio web o presencialmente con un abono de $50.000.

            ¿Cuál es la duración aproximada de un cumpleaños en Happyland?
            • Una hora en el salón y 60-90 minutos de juego según el paquete.
            """.trimIndent(),
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

// Función auxiliar para construir la lista de características de un paquete
fun buildFeaturesList(packageData: Map<String, Any>): List<String> {
    val features = mutableListOf<String>()
    features.add("Duración del salón: ${packageData["duracionSalon"]} minutos")
    features.add("Duración HappyCard: ${packageData["happyCardDuracion"]} minutos")
    if (packageData["distintivoFestejado"] == true) features.add("Incluye distintivo para el festejado")
    if (packageData["piñata"] == true) features.add("Incluye piñata")
    val regalosFestejado = packageData["regaloFestejado"] as? Map<*, *>
    regalosFestejado?.let {
        if (it["loncheraTermica"] == true) features.add("Regalo: Lonchera Térmica")
        if (it["vasoColeccionable"] == true) features.add("Regalo: Vaso Coleccionable")
        features.add("Tickets para el festejado: ${it["tickets"]}")
    }
    val regalosInvitados = packageData["regaloInvitados"] as? Map<*, *>
    regalosInvitados?.let {
        features.add("Tickets para cada invitado: ${it["tickets"]}")
        if (it["salonExclusivo"] == true) features.add("Salón exclusivo")
        if (it["snackCumpleaños"] == true) features.add("Incluye snack para invitados")
    }
    return features
}
