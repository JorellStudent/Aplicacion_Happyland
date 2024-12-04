package com.example.aplicacion_happyland.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacion_happyland.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat


import androidx.compose.material.icons.Icons

@Composable
fun CalendarReservationScreen(navController: NavController, packageType: String) {
    // Instancia de Firebase Firestore y estados para manejar datos y reservas
    val db = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()
    val reservedDates = remember { mutableStateListOf<String>() }
    var selectedDate by remember { mutableStateOf("") } // Fecha seleccionada por el usuario
    var selectedTime by remember { mutableStateOf("") } // Horario seleccionado
    val timeOptions = listOf("11:00 AM", "14:00 PM", "17:00 PM") // Opciones de horario
    var isTimeDropdownExpanded by remember { mutableStateOf(false) } // Dropdown de horarios
    var numberOfKids by remember { mutableIntStateOf(15) } // Número inicial de niños
    var observations by remember { mutableStateOf("") } // Observaciones del usuario
    var paymentMethod by remember { mutableStateOf("Web") } // Método de pago seleccionado
    var isLoading by remember { mutableStateOf(false) } // Indicador de carga
    var packagePrice by remember { mutableIntStateOf(0) } // Precio del paquete
    var totalAmount by remember { mutableIntStateOf(0) } // Total a pagar
    var deposit by remember { mutableIntStateOf(50000) } // Abono inicial

    // Campos adicionales para los datos personales
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Estados para controlar el mes y el año actual en el calendario
    val calendar = Calendar.getInstance()
    val currentMonth = remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    val currentYear = remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }

    // Cargar fechas reservadas y establecer el precio del paquete
    LaunchedEffect(Unit) {
        loadReservedDates(db, reservedDates) // Función para cargar fechas reservadas desde Firebase
        packagePrice = when (packageType) {
            "Súper Happy" -> 13990
            "Happy" -> 11990
            "Extra Happy" -> 16990
            else -> 0
        }
        totalAmount = packagePrice * numberOfKids
    }

    // Interfaz principal con un fondo de color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)) // Fondo verde
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.fondocolor),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Contenido principal de la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permitir desplazamiento vertical
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título principal
            Text(
                text = "Reservación de Cumpleaños",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Navegación entre meses en el calendario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        // Cambiar al mes anterior
                        currentMonth.intValue = (currentMonth.intValue - 1).let {
                            if (it < 0) 11 else it
                        }
                        if (currentMonth.intValue == 11) currentYear.intValue -= 1
                        loadReservedDates(db, reservedDates)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Mes anterior",
                        tint = Color.White
                    )
                }

                // Mostrar el mes y año actual
                Text(
                    text = "${Calendar.getInstance().apply { set(Calendar.MONTH, currentMonth.intValue) }.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${currentYear.intValue}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(
                    onClick = {
                        // Cambiar al mes siguiente
                        currentMonth.intValue = (currentMonth.intValue + 1) % 12
                        if (currentMonth.intValue == 0) currentYear.intValue += 1
                        loadReservedDates(db, reservedDates)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Mes siguiente",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vista del calendario
            CalendarView(
                year = currentYear.intValue,
                month = currentMonth.intValue,
                reservedDates = reservedDates,
                selectedDate = selectedDate,
                onDateSelected = { date -> selectedDate = date }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campos para introducir nombre, apellido y correo
            InputField(label = "Nombre", value = firstName) { firstName = it }
            InputField(label = "Apellido", value = lastName) { lastName = it }
            InputField(
                label = "Correo Electrónico",
                value = email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            ) { email = it }

            Spacer(modifier = Modifier.height(16.dp))

            // Selección de horario
            Text("Selecciona un horario:", fontSize = 16.sp, color = Color.White)
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { isTimeDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedTime.ifEmpty { "Seleccionar Horario" })
                }
                DropdownMenu(
                    expanded = isTimeDropdownExpanded,
                    onDismissRequest = { isTimeDropdownExpanded = false }
                ) {
                    timeOptions.forEach { time ->
                        DropdownMenuItem(
                            onClick = {
                                selectedTime = time
                                isTimeDropdownExpanded = false
                            },
                            text = { Text(time) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Incrementar o decrementar la cantidad de niños
            Text("Número de niños (mínimo 15)", fontSize = 16.sp, color = Color.White)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    if (numberOfKids > 15) numberOfKids -= 1
                    totalAmount = packagePrice * numberOfKids
                }) {
                    Text("-")
                }
                Text(
                    text = numberOfKids.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Button(onClick = {
                    numberOfKids += 1
                    totalAmount = packagePrice * numberOfKids
                }) {
                    Text("+")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar el total a pagar
            Text("Total a pagar: $${totalAmount}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para la cantidad de abono
            InputField(
                label = "Cantidad de abono (mínimo $50,000)",
                value = deposit.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    val abono = it.toIntOrNull() ?: 0
                    deposit = if (abono >= 50000) abono else 50000
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Observaciones adicionales
            InputField(label = "Observaciones", value = observations) { observations = it }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones para seleccionar el método de pago
            PaymentMethodButtons(selectedMethod = paymentMethod) { paymentMethod = it }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para confirmar la reserva
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        makeReservation(
                            date = selectedDate,
                            packageType = packageType,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            numberOfKids = numberOfKids.toString(),
                            observations = observations,
                            paymentMethod = paymentMethod,
                            totalPagar = totalAmount,
                            selectedTime = selectedTime,
                            deposit = deposit,
                            db = db,
                            onSuccess = {
                                loadReservedDates(db, reservedDates)
                                Toast.makeText(navController.context, "Reserva exitosa", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onFailure = { error -> Toast.makeText(navController.context, "Error: $error", Toast.LENGTH_SHORT).show() }
                        )
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedDate.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && selectedTime.isNotEmpty() && deposit >= 50000 && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirmar Reserva", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}



fun makeReservation(
    date: String,
    packageType: String,
    firstName: String,
    lastName: String,
    email: String,
    numberOfKids: String,
    observations: String,
    paymentMethod: String,
    totalPagar: Int,
    selectedTime: String,
    deposit: Int, // Nuevo parámetro
    db: FirebaseFirestore,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val parsedDate = dateFormat.parse(date)
    val timestamp = Timestamp(parsedDate!!)

    val reservationData = hashMapOf(
        "fechaReserva" to timestamp,
        "horario" to selectedTime,
        "paquete" to packageType,
        "nombre" to firstName,
        "apellido" to lastName,
        "correo" to email,
        "cantidadInvitados" to numberOfKids,
        "observaciones" to observations,
        "metodoPago" to paymentMethod,
        "estado" to "pendiente",
        "totalPagar" to totalPagar,
        "abono" to deposit // Se agrega el abono
    )

    db.collection("reservas")
        .add(reservationData)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception.message ?: "Error desconocido") }
}


@Composable
fun CalendarView(
    year: Int,
    month: Int,
    reservedDates: List<String>,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val circleSize = 40.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Días de la semana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("DOM", "LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB").forEach { day ->
                Text(
                    text = day,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Crear el calendario por semanas
        val weeks = (1..daysInMonth).chunked(7)
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { day ->
                    val date = "$year-${(month + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
                    val isReserved = reservedDates.contains(date)

                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .background(
                                color = when {
                                    isReserved -> Color(0xFFFF6B6B)
                                    date == selectedDate -> Color(0xFFFFEB3B)
                                    else -> Color(0xFF8BC34A)
                                },
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                            )
                            .clickable(enabled = !isReserved) {
                                if (!isReserved) onDateSelected(date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.toString(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

fun loadReservedDates(db: FirebaseFirestore, reservedDates: MutableList<String>) {
    db.collection("reservas")
        .get()
        .addOnSuccessListener { documents ->
            reservedDates.clear()
            for (document in documents) {
                val timestamp = document.get("fechaReserva") as? Timestamp
                val formattedDate = timestamp?.toDate()?.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
                }
                formattedDate?.let { reservedDates.add(it) }
            }
        }
}

@Composable
fun InputField(label: String, value: String, keyboardOptions: KeyboardOptions = KeyboardOptions.Default, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PaymentMethodButtons(selectedMethod: String, onMethodSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { onMethodSelected("Web") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMethod == "Web") Color.Red else Color.Gray
            )
        ) {
            Text("Web", color = Color.White)
        }
        Button(
            onClick = { onMethodSelected("Efectivo en local") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMethod == "Efectivo en local") Color.Blue else Color.Gray
            )
        ) {
            Text("Efectivo", color = Color.White)
        }
    }
}
