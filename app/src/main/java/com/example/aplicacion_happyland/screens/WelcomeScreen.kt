package com.example.aplicacion_happyland.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacion_happyland.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun WelcomeScreen(navController: NavController) {
    // Obtener contexto y referencia a FirebaseAuth
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Pantalla principal con fondo oscuro
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF121212) // Fondo oscuro para mejor contraste
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.fondocolor),
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Contenido principal en columna
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo superior
                Image(
                    painter = painterResource(id = R.drawable.logo_happyland),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp)
                )

                // Título principal de bienvenida
                Text(
                    text = "¡Diversión en Familia!\nBienvenido a Happyland",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón para registrarse
                Button(
                    onClick = { navController.navigate("register") }, // Navegar a la pantalla de registro
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Color verde
                ) {
                    Text(text = "¡Regístrate!", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para iniciar sesión
                Button(
                    onClick = { navController.navigate("login") }, // Navegar a la pantalla de inicio de sesión
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)) // Color azul
                ) {
                    Text(text = "Inicia Sesión", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón para verificar si hay sesión activa
                TextButton(onClick = {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        // Si hay una sesión activa, mostrar un mensaje y redirigir al home
                        Toast.makeText(
                            context,
                            "Sesión activa como ${currentUser.email}",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true } // Limpiar la pila de navegación
                        }
                    } else {
                        // Mostrar un mensaje si no hay sesión activa
                        Toast.makeText(context, "No hay sesión activa", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    // Texto para informar sobre la sesión activa
                    Text(
                        text = "¿Ya tienes sesión activa?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
