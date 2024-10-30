package com.example.aplicacion_happyland.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacion_happyland.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.fondoburbujas),
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Contenido del formulario
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de correo
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de inicio de sesión
                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            loginUser(email, password, auth, context, navController)
                        } else {
                            Toast.makeText(context, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Sesión", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Enlace a registro
                TextButton(onClick = { navController.navigate("register") }) {
                    Text(
                        text = "¿No tienes cuenta? Regístrate",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// Función para iniciar sesión
fun loginUser(
    email: String,
    password: String,
    auth: FirebaseAuth,
    context: android.content.Context,
    navController: NavController
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Inicio de sesión exitoso
                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }  // Elimina la pantalla de login de la pila
                }
            } else {
                handleLoginError(task.exception, context)  // Manejo de errores
            }
        }
}

// Manejo de errores de inicio de sesión
fun handleLoginError(
    exception: Exception?,
    context: android.content.Context
) {
    when (exception) {
        is FirebaseAuthInvalidCredentialsException -> {
            Toast.makeText(context, "Contraseña incorrecta. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
        }
        is FirebaseAuthInvalidUserException -> {
            Toast.makeText(context, "Usuario no registrado. Verifica tu correo.", Toast.LENGTH_SHORT).show()
        }
        else -> {
            Toast.makeText(context, "Error: ${exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
