@file:Suppress("DEPRECATION")

package com.example.aplicacion_happyland.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.util.*

@Composable
fun AddCardScreen(navController: NavController) {
    var cardNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

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
                    text = "Agregar Tarjeta",
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

                Button(
                    onClick = {
                        if (cardNumber.isEmpty()) {
                            Toast.makeText(context, "Por favor ingresa un número de tarjeta", Toast.LENGTH_SHORT).show()
                        } else {
                            saveCardToFirestore(cardNumber, db, context) {
                                navController.navigate("home") {
                                    popUpTo("addcard") { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Guardar Tarjeta", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}

fun saveCardToFirestore(
    cardNumber: String,
    db: FirebaseFirestore,
    context: android.content.Context,
    onSuccess: () -> Unit
) {
    val cardData = hashMapOf(
        "numero" to cardNumber,
        "saldoEfectivo" to 0.0,
        "saldoBonus" to 0.0,
        "tokens" to 0,
        "tickets" to 0,
        "fechaCreacion" to Date()
    )

    db.collection("Tarjetas").document(cardNumber).set(cardData)
        .addOnSuccessListener {
            Toast.makeText(context, "Tarjeta guardada exitosamente", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al guardar la tarjeta: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("AddCardScreen", "Error al guardar la tarjeta", e)
        }
}

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
