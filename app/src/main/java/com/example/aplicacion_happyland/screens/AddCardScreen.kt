package com.example.aplicacion_happyland.screens

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.widget.Toast
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import java.nio.charset.Charset

@Composable
fun AddCardScreen(navController: NavController) {
    // Estado para almacenar el número de tarjeta ingresado por el usuario
    var codigo by remember { mutableStateOf("") }

    // Contexto y referencias para la actividad y Firebase
    val context = LocalContext.current
    val activity = context as androidx.activity.ComponentActivity
    val db = FirebaseFirestore.getInstance()

    // Adaptador NFC para habilitar la lectura de tarjetas
    val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    // Mostrar mensaje al usuario si NFC no está disponible en el dispositivo
    LaunchedEffect(nfcAdapter) {
        if (nfcAdapter == null) {
            Toast.makeText(context, "NFC no está disponible en este dispositivo", Toast.LENGTH_LONG).show()
        }
    }

    // Estructura principal de la pantalla
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent) // Fondo transparente
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.fondocolor),
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Título de la pantalla
                Text(
                    text = "Buscar Tarjeta",
                    fontSize = 28.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Campo de texto para ingresar el número de tarjeta
                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = formatCardNumber(it) }, // Formatear automáticamente
                    label = { Text("Número de Tarjeta") },
                    placeholder = { Text("0000-0000-0000-0000") }, // Formato esperado
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp), // Esquinas redondeadas
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para buscar la tarjeta ingresada
                Button(
                    onClick = {
                        if (isCardNumberValid(codigo)) {
                            // Validar y buscar la tarjeta en Firebase
                            fetchCardData(codigo, db, context, navController)
                        } else {
                            Toast.makeText(context, "Número de tarjeta inválido", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Color verde
                ) {
                    Text("Buscar Tarjeta", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }

    // Habilitar el lector NFC para leer tarjetas mientras la pantalla está activa
    DisposableEffect(Unit) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, context::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) // Evitar múltiples instancias
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        )

        // Activar la escucha de tarjetas NFC
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, null, null)

        // Deshabilitar NFC cuando la pantalla no esté activa
        onDispose {
            try {
                nfcAdapter?.disableForegroundDispatch(activity)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al deshabilitar NFC", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// Procesar la información de una tarjeta NFC
fun handleNfcIntent(intent: Intent, db: FirebaseFirestore, context: Context, navController: NavController) {
    val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) // Obtener el tag NFC
    val ndef = Ndef.get(tag)

    if (ndef != null) {
        try {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            val text = ndefMessage.records[0].payload.toString(Charset.forName("UTF-8")) // Leer contenido
            ndef.close()

            // Buscar la tarjeta en Firebase con el contenido leído
            fetchCardData(text, db, context, navController)
        } catch (e: Exception) {
            Toast.makeText(context, "Error al leer la tarjeta NFC", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "No se pudo leer la tarjeta NFC", Toast.LENGTH_SHORT).show()
    }
}

// Buscar tarjeta en Firebase
fun fetchCardData(codigo: String, db: FirebaseFirestore, context: Context, navController: NavController) {
    db.collection("tarjetas").whereEqualTo("codigo", codigo).get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val cardData = documents.documents[0].data
                if (cardData != null) {
                    val cardNumber = cardData["codigo"] as? String ?: "0000-0000-0000-0000"
                    val userName = cardData["nombre"] as? String ?: "Desconocido"
                    val saldoEfectivo = (cardData["saldoEfectivo"] as? Long)?.toInt() ?: 0
                    val saldoBonus = (cardData["saldoBonus"] as? Long)?.toInt() ?: 0
                    val tickets = (cardData["tickets"] as? Long)?.toInt() ?: 0
                    val tokens = (cardData["tokens"] as? Long)?.toInt() ?: 0

                    Toast.makeText(context, "Tarjeta encontrada: $userName", Toast.LENGTH_SHORT).show()

                    // Navegar a la pantalla principal con los datos de la tarjeta
                    navController.navigate(
                        "home?codigo=$cardNumber&nombre=$userName&saldoEfectivo=$saldoEfectivo&saldoBonus=$saldoBonus&tickets=$tickets&tokens=$tokens"
                    ) {
                        popUpTo("addcard") { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Datos incompletos en la tarjeta", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Tarjeta no encontrada", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al buscar la tarjeta: ${it.message}", Toast.LENGTH_SHORT).show()
        }
}

// Formatear el número de tarjeta para mayor legibilidad
fun formatCardNumber(input: String): String {
    val cleanInput = input.replace("-", "").take(16) // Limitar a 16 dígitos
    return cleanInput.chunked(4).joinToString("-") // Agrupar en bloques de 4 separados por guiones
}

// Validar si el número de tarjeta ingresado es válido
fun isCardNumberValid(cardNumber: String): Boolean {
    return cardNumber.length == 19 && cardNumber.matches(Regex("\\d{4}-\\d{4}-\\d{4}-\\d{4}"))
}