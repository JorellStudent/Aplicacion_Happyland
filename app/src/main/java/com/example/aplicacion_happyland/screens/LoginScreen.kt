package com.example.aplicacion_happyland.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacion_happyland.R
import com.example.aplicacion_happyland.utils.saveLoginState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@Composable
fun LoginScreen(navController: NavController) {
    // Variables de estado para correo, contraseña, y visibilidad de carga
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Contexto y autenticación de Firebase
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent) // Fondo transparente
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.fondoburbujas), // Recurso de fondo
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Mostrar un indicador de carga si `isLoading` es verdadero
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Contenido del formulario
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(32.dp), // Margen alrededor del formulario
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Título principal
                    Text(
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Campo de entrada para el correo
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de entrada para la contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        // Mostrar/Ocultar la contraseña
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (passwordVisible) R.drawable.ocultar_icono else R.drawable.mostrar_icono
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Image(
                                    painter = painterResource(id = icon),
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón para iniciar sesión
                    Button(
                        onClick = {
                            // Validar campos no vacíos antes de proceder
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                isLoading = true // Mostrar indicador de carga
                                loginUser(email, password, auth, context, navController) {
                                    isLoading = false // Ocultar indicador después del proceso
                                }
                            } else {
                                Toast.makeText(context, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp), // Altura del botón
                        shape = RoundedCornerShape(28.dp), // Esquinas redondeadas
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)) // Color azul del botón
                    ) {
                        Text("Iniciar Sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Enlace para registrar una cuenta nueva
                    TextButton(onClick = { navController.navigate("register") }) {
                        Text(
                            text = "¿No tienes cuenta? Regístrate",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
    navController: NavController,
    onComplete: () -> Unit // Callback para finalizar el proceso
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            onComplete() // Llamar al callback cuando finalice
            if (task.isSuccessful) {
                // Guardar el estado del inicio de sesión
                saveLoginState(context, true)
                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                // Navegar a la pantalla principal
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                // Manejar errores de inicio de sesión
                handleLoginError(task.exception, context)
            }
        }
}

// Función para manejar errores específicos de Firebase
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
