package com.example.aplicacion_happyland.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavHostController
import com.example.aplicacion_happyland.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var cardData by remember { mutableStateOf<Map<String, Any>?>(null) }
    val cart = remember { mutableStateListOf<Pair<String, Double>>() }
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // Cargar datos de la tarjeta desde Firebase Firestore
    LaunchedEffect(Unit) {
        val cardNumber = "1234-5678-9101-1121"
        db.collection("Tarjetas").document(cardNumber).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    cardData = document.data
                } else {
                    Toast.makeText(context, "Tarjeta no encontrada", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar la tarjeta", Toast.LENGTH_SHORT).show()
            }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
                    .padding(16.dp)
            ) {
                Text("Perfil", color = Color.White, fontSize = 20.sp, modifier = Modifier.clickable { /* Navegar a perfil */ })
                Spacer(modifier = Modifier.height(8.dp))
                Text("Configuración", color = Color.White, fontSize = 20.sp, modifier = Modifier.clickable { /* Navegar a configuración */ })
                Spacer(modifier = Modifier.height(8.dp))
                Text("Cerrar Sesión", color = Color.White, fontSize = 20.sp, modifier = Modifier.clickable { /* Cerrar sesión */ })
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Happyland", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Navegar al perfil */ }) {
                            Image(
                                painter = painterResource(id = R.drawable.usuario_icono),
                                contentDescription = "Perfil",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF000000))
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Fondo de imagen
                Image(
                    painter = painterResource(id = R.drawable.general_fondo),
                    contentDescription = "Fondo de pantalla",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    CardDetailsSection(navController, cardData)
                    Spacer(modifier = Modifier.height(16.dp))
                    BirthdaySection(navController)
                    Spacer(modifier = Modifier.height(16.dp))
                    TicketPurchaseOptions(cart) { ticket ->
                        cart.add(ticket)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ExclusiveRechargeCarousel(cart) { recharge ->
                        cart.add(recharge)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (cart.isNotEmpty()) {
                        CheckoutSection(cart) { payAmount ->
                            handlePayment(payAmount, cardData, db, context) {
                                cart.clear()
                                Toast.makeText(context, "Pago exitoso", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardDetailsSection(
    navController: NavHostController,
    cardData: Map<String, Any>?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4A148C), RoundedCornerShape(12.dp))
            .padding(16.dp)
            .clickable { navController.navigate("addcard") }
    ) {
        Text(text = "Nombre del Usuario", color = Color.White, fontWeight = FontWeight.Bold)
        Text(
            text = "N° TARJETA: ${cardData?.get("numero") as? String ?: "0000-0000-0000-0000"}",
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column { Text("Saldo Efectivo: ${cardData?.get("saldoEfectivo") ?: 0.0}", color = Color.White) }
            Column { Text("Bonus: ${cardData?.get("saldoBonus") ?: 0.0}", color = Color.White) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column { Text("eTickets: ${cardData?.get("tickets") ?: 0}", color = Color.White) }
            Column { Text("Tokens: ${cardData?.get("tokens") ?: 0}", color = Color.White) }
        }
    }
}

@Composable
fun BirthdaySection(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E88E5), RoundedCornerShape(8.dp))
            .padding(16.dp)
            .clickable { navController.navigate("birthdayReservation") }, // Navegar a BirthdayReservationScreen
        contentAlignment = Alignment.Center
    ) {
        Text(
            "HAPPY BDAY\nEl mejor cumpleaños para tus hijos está en Happyland",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun ExclusiveRechargeCarousel(cart: MutableList<Pair<String, Double>>, onAddToCart: (Pair<String, Double>) -> Unit) {
    val rechargeOptions = listOf(
        Triple("Recarga $35.000", "Bonus 22.000", 35000.0),
        Triple("Recarga $25.000", "Bonus 15.000", 25000.0),
        Triple("Recarga $20.000", "Bonus 12.000", 20000.0),
        Triple("Recarga $15.000", "Bonus 6.000", 15000.0)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(rechargeOptions) { (recarga, bonus, precio) ->
            RechargeOption(recarga, bonus, precio, Color.Gray) {
                onAddToCart(recarga to precio)
            }
        }
    }
}

@Composable
fun TicketPurchaseOptions(cart: MutableList<Pair<String, Double>>, onAddToCart: (Pair<String, Double>) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFEB3B), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text("COMPRA DE TICKETS", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        TicketOption("500 e-tickets", "$3.000", 3000.0, Color.Green) {
            onAddToCart("500 e-tickets" to 3000.0)
        }
        TicketOption("1000 e-tickets", "$4.500", 4500.0, Color.Blue) {
            onAddToCart("1000 e-tickets" to 4500.0)
        }
    }
}

@Composable
fun RechargeOption(recarga: String, bonus: String, precio: Double, color: Color, onAddToCart: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .background(color, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { onAddToCart() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(recarga, color = Color.White, fontWeight = FontWeight.Bold)
        Text("+", color = Color.White, fontWeight = FontWeight.Bold)
        Text(bonus, color = Color.White, fontWeight = FontWeight.Bold)
        Text("=", color = Color.White, fontWeight = FontWeight.Bold)
        Text("$$precio", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TicketOption(etickets: String, price: String, value: Double, color: Color, onAddToCart: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(color, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { onAddToCart() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(etickets, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(price, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun CheckoutSection(cart: List<Pair<String, Double>>, onPay: (Double) -> Unit) {
    val total = cart.sumOf { it.second }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4CAF50), RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Total a Pagar: $$total", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onPay(total) }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("PAGAR $$total", fontSize = 18.sp, color = Color.White)
        }
    }
}

fun handlePayment(
    amount: Double,
    cardData: Map<String, Any>?,
    db: FirebaseFirestore,
    context: android.content.Context,
    onSuccess: () -> Unit
) {
    val cardNumber = cardData?.get("numero") as? String ?: return
    db.collection("Tarjetas").document(cardNumber).update("saldoEfectivo", amount)
        .addOnSuccessListener {
            Toast.makeText(context, "Pago realizado con éxito", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error en el pago", Toast.LENGTH_SHORT).show()
        }
}
