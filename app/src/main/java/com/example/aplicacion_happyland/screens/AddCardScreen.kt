@file:Suppress("DEPRECATION")

package com.example.aplicacion_happyland.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacion_happyland.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator

@Composable
fun AddCardScreen(navController: NavController) {
    var cardNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    // QR Launcher para escanear la tarjeta
    val qrLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val contents = IntentIntegrator.parseActivityResult(
                result.resultCode, result.data
            )?.contents
            if (contents != null) {
                cardNumber = contents
                Toast.makeText(context, "QR Escaneado: $contents", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondoburbujas),
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Cargar o Buscar Tarjeta",
                    fontSize = 28.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = formatCardNumber(it) },
                    label = { Text("Número de Tarjeta") },
                    placeholder = { Text("0000-0000-0000-0000") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Button(
                    onClick = {
                        val integrator = IntentIntegrator(context as android.app.Activity)
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        integrator.setPrompt("Escanea el código QR de la tarjeta")
                        integrator.setCameraId(0)
                        integrator.setBeepEnabled(true)
                        qrLauncher.launch(integrator.createScanIntent())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Escanear QR", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para cargar o buscar la tarjeta en Firebase
                Button(
                    onClick = {
                        if (cardNumber.isEmpty()) {
                            Toast.makeText(context, "Por favor ingresa un número de tarjeta", Toast.LENGTH_SHORT).show()
                        } else {
                            // Busca la tarjeta en Firebase
                            db.collection("Tarjetas").document(cardNumber).get()
                                .addOnSuccessListener { document ->
                                    if (document != null && document.exists()) {
                                        // Si la tarjeta existe, muestra el mensaje y navega al HomeScreen
                                        Toast.makeText(context, "Tarjeta encontrada", Toast.LENGTH_SHORT).show()
                                        navController.navigate("home") {
                                            popUpTo("addcard") { inclusive = true }
                                        }
                                    } else {
                                        // Si no se encuentra, muestra un mensaje de error
                                        Toast.makeText(context, "Tarjeta no encontrada o número incorrecto", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al buscar la tarjeta", Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Cargar o Buscar Tarjeta", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}

// Función para formatear el número de tarjeta
fun formatCardNumber(input: String): String {
    val cleanInput = input.replace("-", "").take(16)
    val formatted = StringBuilder()

    for (i in cleanInput.indices) {
        formatted.append(cleanInput[i])
        if ((i + 1) % 4 == 0 && i != cleanInput.lastIndex) {
            formatted.append("-")
        }
    }

    return formatted.toString()
}


//holholahola