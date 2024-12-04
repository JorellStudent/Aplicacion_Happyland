package com.example.aplicacion_happyland

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aplicacion_happyland.screens.*
import com.example.aplicacion_happyland.ui.theme.Aplicacion_HappylandTheme
import com.example.aplicacion_happyland.utils.NfcUtils
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración de NFC
        configureNfcAdapter()

        setContent {
            Aplicacion_HappylandTheme {
                AppNavigator()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableNfcForegroundDispatch()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED || intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            handleNfcIntent(intent)
        }
    }

    private fun configureNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        when {
            nfcAdapter == null -> {
                Toast.makeText(this, "NFC no está disponible en este dispositivo", Toast.LENGTH_LONG).show()
            }
            !nfcAdapter!!.isEnabled -> {
                Toast.makeText(this, "Por favor, activa NFC en la configuración", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun enableNfcForegroundDispatch() {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        )
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    private fun disableNfcForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun handleNfcIntent(intent: Intent) {
        NfcUtils.processNfcTag(
            intent = intent,
            db = FirebaseFirestore.getInstance(),
            context = this,
            onSuccess = { tagData ->
                Toast.makeText(this, "Tarjeta detectada: $tagData", Toast.LENGTH_SHORT).show()
                navigateToHomeWithCard(tagData)
            },
            onError = { errorMessage ->
                Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun navigateToHomeWithCard(tagData: String) {
        FirebaseFirestore.getInstance().collection("tarjetas")
            .whereEqualTo("codigo", tagData)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    setContent {
                        Aplicacion_HappylandTheme {
                            AppNavigator(cardNumber = tagData)
                        }
                    }
                } else {
                    Toast.makeText(this, "Tarjeta no encontrada en Firebase", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al buscar tarjeta en Firebase", Toast.LENGTH_SHORT).show()
            }
    }
}

@Composable
fun AppNavigator(cardNumber: String? = null) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                navController = navController,
                cardNumber = cardNumber,
                onAddCardClick = { navController.navigate("addcard") },
                onLogoutClick = {
                    navController.navigate("welcome") {
                        popUpTo("home") { inclusive = true } // Elimina el historial de navegación
                    }
                }
            )
        }
        composable("addcard") {
            AddCardScreen(navController)
        }
        composable("birthdayReservation") {
            BirthdayReservationScreen(navController)
        }
        composable("calendar/{packageType}") { backStackEntry ->
            val packageType = backStackEntry.arguments?.getString("packageType") ?: "UNKNOWN"
            CalendarReservationScreen(navController, packageType)
        }
        composable(
            route = "prizesScreen/{cardNumber}/{tickets}",
            arguments = listOf(
                navArgument("cardNumber") { type = NavType.StringType },
                navArgument("tickets") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val cardNumberFromRoute = backStackEntry.arguments?.getString("cardNumber") ?: ""
            val ticketsFromRoute = backStackEntry.arguments?.getInt("tickets") ?: 0
            PrizesScreen(navController = navController, cardNumber = cardNumberFromRoute, ticketsIniciales = ticketsFromRoute)
        }
        composable("welcome") {
            WelcomeScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }

    }
}
