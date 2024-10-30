package com.example.aplicacion_happyland.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun WelcomeScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.fondoconhappy), // Reemplaza con tu imagen
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Contenido sobre la imagen de fondo
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo superior
                Image(
                    painter = painterResource(id = R.drawable.logo_happyland), // Reemplaza con tu logo
                    contentDescription = "Logo",
                    modifier = Modifier.size(300.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Título principal
                Text(
                    text = "'Diversión en Familia'\nAplicación Móvil Happyland",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Botón "Sign up free"
                Button(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text(text = "¡Regístrate!", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón "Continue with Google"
                OutlinedButton(
                    onClick = { /* Acción con Google */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp), // Borde más grueso
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White, // Fondo blanco
                        contentColor = Color.Black // Texto e ícono negros
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_icono), // Logo de Google
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Continúa con Google!", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón "Continue with Facebook"
                OutlinedButton(
                    onClick = { /* Acción con Facebook */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp), // Borde más grueso
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White, // Fondo blanco
                        contentColor = Color.Black // Texto e ícono negros
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.facebook_icono), // Logo de Facebook
                        contentDescription = "Facebook Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Continúa con Facebook", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Enlace "Log in"
                TextButton(onClick = { navController.navigate("login") }) {
                    Text(
                        text = "Inicia Sesión",
                        fontSize = 18.sp, // Aumentamos el tamaño del texto
                        fontWeight = FontWeight.Bold, // Hacemos el texto más grueso
                        color = Color.White, // Color blanco para mayor contraste
                        textAlign = TextAlign.Center)
                }
            }
        }
    }
}
