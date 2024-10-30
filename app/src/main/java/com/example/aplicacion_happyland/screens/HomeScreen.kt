package com.example.aplicacion_happyland.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aplicacion_happyland.R
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var tarjetas by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var tarjetasActivas by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    // Recupera las tarjetas de Firebase Firestore
    LaunchedEffect(Unit) {
        db.collection("Tarjetas").get()
            .addOnSuccessListener { result ->
                val todasTarjetas = result.documents.mapNotNull { it.data }
                tarjetas = todasTarjetas
                tarjetasActivas = todasTarjetas // Inicialmente, todas las tarjetas están activas
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar tarjetas", Toast.LENGTH_SHORT).show()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hola, Happylander",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Image(
                            painter = painterResource(id = R.drawable.usuario_icono),
                            contentDescription = "Perfil",
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.LightGray, CircleShape)
                        )
                    }
                },
                modifier = Modifier.background(Color(0xFF2196F3))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.fondoburbujas),
                contentDescription = "Fondo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Contenido principal
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (tarjetasActivas.isEmpty()) {
                    item { BannerPromocional(navController) }
                } else {
                    items(tarjetasActivas) { tarjeta ->
                        TarjetaItem(tarjeta, navController) { tarjetaADesactivar ->
                            tarjetasActivas = tarjetasActivas.filter { it != tarjetaADesactivar }
                        }
                    }
                }

                item { BotonesGrid(navController) }
            }
        }
    }
}

@Composable
fun BannerPromocional(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Color.Yellow, RoundedCornerShape(8.dp))
            .clickable { navController.navigate("addcard") }, // Navega a la pantalla de agregar tarjeta
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "¡Agrega tu tarjeta Aquí!!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun BotonesGrid(navController: NavHostController) {
    val elementosGrid = listOf(
        Triple(R.drawable.formulario_icono, "Formularios", "formularios"),
        Triple(R.drawable.mapa_icono, "Mapas", "mapas"),
        Triple(R.drawable.cumpleano_icono, "Cumpleaños", "cumpleaños"),
        Triple(R.drawable.tips_icono, "Tips", "tips"),
        Triple(R.drawable.configuracion_icono, "Configuración", "configuracion"),
        Triple(R.drawable.otros_icono, "Otros", "otros")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        elementosGrid.chunked(3).forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                fila.forEach { (icono, etiqueta, ruta) ->
                    BotonGrid(icono, etiqueta) {
                        navController.navigate(ruta)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BotonGrid(iconoId: Int, etiqueta: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = iconoId),
            contentDescription = etiqueta,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = etiqueta, fontSize = 14.sp, color = Color.White)
    }
}

@Composable
fun TarjetaItem(
    tarjeta: Map<String, Any>,
    navController: NavHostController,
    onDesactivar: (Map<String, Any>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(16.dp)
            .clickable {
                navController.navigate("addcard")
            }
    ) {
        Text("Número: ${tarjeta["numero"]}", fontSize = 18.sp)
        Text("Saldo Efectivo: ${tarjeta["saldoEfectivo"]}", fontSize = 16.sp)
        Text("Saldo Bonus: ${tarjeta["saldoBonus"]}", fontSize = 16.sp)
        Text("Tokens: ${tarjeta["tokens"]}", fontSize = 16.sp)
        Text("Tickets: ${tarjeta["tickets"]}", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para desactivar tarjeta
        Button(
            onClick = { onDesactivar(tarjeta) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Desactivar", color = Color.White)
        }
    }
}
