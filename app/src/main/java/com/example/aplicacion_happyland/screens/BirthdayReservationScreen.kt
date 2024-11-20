package com.example.aplicacion_happyland.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun BirthdayReservationScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "PAQUETES DE CUMPLEAÑOS",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF003366),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { BirthdayPackageCard("HAPPY", "$11.990", listOf("Salón exclusivo", "1 hora de juego", "500 E-tickets"), Color(0xFF9C27B0)) }
            item { BirthdayPackageCard("SUPER HAPPY", "$13.990", listOf("Salón exclusivo", "1:30 hrs de juego", "1,000 E-tickets", "Lonchera térmica"), Color(0xFF673AB7)) }
            item { BirthdayPackageCard("EXTRA HAPPY", "$16.990", listOf("Salón exclusivo", "1:30 hrs de juego", "2,000 E-tickets", "Piñata"), Color(0xFFE91E63)) }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Acción para reservar */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("RESERVA AQUÍ", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Acción para preguntas frecuentes */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Preguntas Frecuentes", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BirthdayPackageCard(title: String, price: String, features: List<String>, backgroundColor: Color) {
    Column(
        modifier = Modifier
            .width(250.dp)
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "cumpleaños",
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = price,
            fontSize = 28.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            features.forEach { feature ->
                Text(
                    text = "• $feature",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Mínimo 15 invitados",
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}
