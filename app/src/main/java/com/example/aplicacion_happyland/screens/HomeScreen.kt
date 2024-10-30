package com.example.aplicacion_happyland.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aplicacion_happyland.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Hola, Happylander", fontWeight = FontWeight.Bold)
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
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { AccesoRapido() }
            item { BarraBusqueda() }
            item { BannerPromocional() }
            item { BotonesGrid() }
        }
    }
}

@Composable
fun AccesoRapido() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BotonAcceso("Recargar")
        BotonAcceso("Canjear")
        BotonAcceso("Cámara")
    }
}

@Composable
fun BotonAcceso(texto: String) {
    Button(
        onClick = { /* Acción aquí */ },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(text = texto, fontSize = 16.sp)
    }
}

@Composable
fun BarraBusqueda() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Búsqueda...") },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(8.dp))
    )
}

@Composable
fun BannerPromocional() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Yellow, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "¡Agrega tu tarjeta Aquí!!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BotonesGrid() {
    val elementosGrid = listOf(
        Pair(R.drawable.formulario_icono, "Formularios"),
        Pair(R.drawable.mapa_icono, "Mapas"),
        Pair(R.drawable.cumpleano_icono, "Cumpleaños"),
        Pair(R.drawable.tips_icono, "Tips"),
        Pair(R.drawable.configuracion_icono, "Configuración"),
        Pair(R.drawable.otros_icono, "Otros")
    )

    Column {
        elementosGrid.chunked(3).forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                fila.forEach { (icono, etiqueta) ->
                    BotonGrid(icono, etiqueta)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BotonGrid(iconoId: Int, etiqueta: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = iconoId),
            contentDescription = etiqueta,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = etiqueta, fontSize = 14.sp)
    }
}
