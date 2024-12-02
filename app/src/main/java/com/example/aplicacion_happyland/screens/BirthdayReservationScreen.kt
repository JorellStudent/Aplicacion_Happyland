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
    val db = FirebaseFirestore.getInstance()
    val packages = remember { mutableStateListOf<Map<String, Any>>() }
    var isLoading by remember { mutableStateOf(true) }
    var showFaq by remember { mutableStateOf(false) }

    // Colores para los paquetes
    val packageColors = listOf(
        Color(0xFFE57373), // Rojo
        Color(0xFF81C784), // Verde
        Color(0xFF64B5F6), // Azul
        Color(0xFFFFD54F), // Amarillo
        Color(0xFFBA68C8)  // Morado
    )

    // Obtener los paquetes desde Firebase y ordenarlos de mayor a menor precio
    LaunchedEffect(Unit) {
        db.collection("paquetes")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    packages.clear()
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo del CalendarReservationScreen
        Image(
            painter = painterResource(id = R.drawable.general_fondo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Scroll vertical principal
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

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                if (packages.isEmpty()) {
                    Text(
                        text = "No hay paquetes disponibles",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                } else {
                    // Scroll horizontal para paquetes
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(540.dp), // Altura uniforme para todas las tarjetas
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(packages.size) { index ->
                            val packageData = packages[index]
                            val title = packageData["nombre"].toString()
                            val price = "$${packageData["precio"].toString()}"
                            val features = buildFeaturesList(packageData)
                            val color = packageColors[index % packageColors.size] // Color único por paquete

                            BirthdayPackageCard(
                                title = title,
                                price = price,
                                features = features,
                                backgroundColor = color,
                                onClick = {
                                    navController.navigate("calendar/$title")
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para mostrar preguntas frecuentes
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

            if (showFaq) {
                FaqSection()
            }
        }
    }
}

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
        Text(
            text = title,
            fontSize = 32.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = price,
            fontSize = 24.sp,
            color = Color.Yellow,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

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

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

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
