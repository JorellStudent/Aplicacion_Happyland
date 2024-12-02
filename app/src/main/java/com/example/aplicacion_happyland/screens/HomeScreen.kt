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
import androidx.compose.material.icons.filled.Add
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
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    cardNumber: String? = null,
    onAddCardClick: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Estados para datos de la tarjeta
    var userName by remember { mutableStateOf("Desconocido") }
    var userLastName by remember { mutableStateOf("Desconocido") }
    var saldoEfectivo by remember { mutableIntStateOf(0) }
    var saldoBonus by remember { mutableIntStateOf(0) }
    var tickets by remember { mutableIntStateOf(0) }
    var tokens by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(cardNumber != null) }

    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // Buscar tarjeta en Firebase
    LaunchedEffect(cardNumber) {
        if (!cardNumber.isNullOrEmpty()) {
            isLoading = true
            try {
                val snapshot = db.collection("tarjetas").whereEqualTo("codigo", cardNumber).get().await()
                if (snapshot.isEmpty) {
                    Toast.makeText(context, "Tarjeta no encontrada", Toast.LENGTH_SHORT).show()
                } else {
                    val cardData = snapshot.documents[0].data
                    userName = cardData?.get("nombre") as? String ?: "Desconocido"
                    userLastName = cardData?.get("apellido") as? String ?: "Desconocido"
                    saldoEfectivo = (cardData?.get("saldoEfectivo") as? Long)?.toInt() ?: 0
                    saldoBonus = (cardData?.get("saldoBonus") as? Long)?.toInt() ?: 0
                    tickets = (cardData?.get("tickets") as? Long)?.toInt() ?: 0
                    tokens = (cardData?.get("tokens") as? Long)?.toInt() ?: 0

                    Toast.makeText(context, "Tarjeta cargada exitosamente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar la tarjeta: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent() }
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Happyland",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color.White
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
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
                    // Fondo de pantalla
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
                        CardDetailsSection(
                            cardNumber = cardNumber ?: "0000-0000-0000-0000",
                            userName = "$userName $userLastName",
                            saldoEfectivo = saldoEfectivo,
                            saldoBonus = saldoBonus,
                            tickets = tickets,
                            tokens = tokens,
                            onAddCardClick = onAddCardClick
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        BirthdaySection(navController)
                        Spacer(modifier = Modifier.height(16.dp))
                        TicketPurchaseOptions()
                        Spacer(modifier = Modifier.height(16.dp))
                        ExclusiveRechargeCarousel()
                    }
                }
            }
        }
    }
}

@Composable
fun CardDetailsSection(
    cardNumber: String,
    userName: String,
    saldoEfectivo: Int,
    saldoBonus: Int,
    tickets: Int,
    tokens: Int,
    onAddCardClick: () -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4A148C), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Usuario: $userName",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = "N° TARJETA: $cardNumber",
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column { Text("Saldo Efectivo: ${numberFormat.format(saldoEfectivo)}", color = Color.White) }
            Column { Text("Bonus: ${numberFormat.format(saldoBonus)}", color = Color.White) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column { Text("eTickets: ${numberFormat.format(tickets)}", color = Color.White) }
            Column { Text("Tokens: ${numberFormat.format(tokens)}", color = Color.White) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAddCardClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Agregar Tarjeta", color = Color.White)
        }
    }
}

@Composable
fun DrawerContent() {
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

@Composable
fun BirthdaySection(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E88E5), RoundedCornerShape(8.dp))
            .padding(16.dp)
            .clickable { navController.navigate("birthdayReservation") },
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
fun ExclusiveRechargeCarousel() {
    val rechargeOptions = listOf(
        Triple("Recarga $35.000", "Bonus 22.000", 35000),
        Triple("Recarga $25.000", "Bonus 15.000", 25000),
        Triple("Recarga $20.000", "Bonus 12.000", 20000),
        Triple("Recarga $15.000", "Bonus 6.000", 15000)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(rechargeOptions) { (recarga, bonus, precio) ->
            Column(
                modifier = Modifier
                    .width(150.dp)
                    .background(Color.Gray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .clickable { /* Manejo de clic aquí */ },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(recarga, color = Color.White, fontWeight = FontWeight.Bold)
                Text("+", color = Color.White, fontWeight = FontWeight.Bold)
                Text(bonus, color = Color.White, fontWeight = FontWeight.Bold)
                Text("=", color = Color.White, fontWeight = FontWeight.Bold)
                Text("$precio", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TicketPurchaseOptions() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFEB3B), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text("COMPRA DE TICKETS", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("500 e-tickets - $3.000", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Green)
        Text("1000 e-tickets - $4.500", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Blue)
    }
}
