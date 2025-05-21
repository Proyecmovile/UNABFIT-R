package com.yorguisanchez.unabfit_r

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsScreen(navController: NavController) {

    val calendarColor = Color(0xFFBDBDBD)
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Scaffold(
        topBar = {
            Surface(
                color = calendarColor,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "UNABFIT-R",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color(0xFF424242),
                        modifier = Modifier
                            .padding(top = 8.dp)
                    )
                    IconButton(
                        onClick = {  },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                    IconButton(
                        onClick = {  },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = calendarColor,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigate("Home") }) {
                        Icon(Icons.Default.Home, contentDescription = "Inicio")
                    }
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Calendario")
                    }
                    IconButton(onClick = { navController.navigate("Reservations") }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favoritos")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                DatePicker(
                    onDateSelected = { year, month, day ->
                        selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year)

                        val calendar = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _: TimePicker, hourOfDay: Int, minute: Int ->
                                selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = calendarColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (selectedDate.isNotEmpty()) "Fecha: $selectedDate" else "Fecha no seleccionada",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedTime.isNotEmpty()) "Hora: $selectedTime" else "Hora no seleccionada",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val userEmail = auth.currentUser?.email       // correo del usuario logueado
                    if (userEmail == null) {
                        Toast.makeText(context, "Inicia sesión para reservar", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val today = sdf.format(Date())

                    if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
                        Toast.makeText(context, "Selecciona una fecha y hora", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (sdf.parse(selectedDate)?.before(sdf.parse(today)) == true) {
                        Toast.makeText(context, "No se pueden hacer reservas en fechas anteriores", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val db = Firebase.firestore
                    db.collection("reservaciones")
                        .whereEqualTo("fecha", selectedDate)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (documents.size() >= 10) {
                                Toast.makeText(context, "Ya hay 10 reservas para este día", Toast.LENGTH_SHORT).show()
                            } else {
                                val nuevaReserva = hashMapOf(
                                    "usuarioEmail" to userEmail,
                                    "fecha" to selectedDate,
                                    "hora" to selectedTime,
                                    "timestamp" to FieldValue.serverTimestamp()
                                )
                                db.collection("reservaciones")
                                    .add(nuevaReserva)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Reserva guardada exitosamente", Toast.LENGTH_SHORT).show()
                                        selectedDate = ""
                                        selectedTime = ""
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Error al guardar la reserva", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al verificar reservas", Toast.LENGTH_SHORT).show()
                        }
                },
                colors = ButtonDefaults.buttonColors(containerColor = calendarColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Reservar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(onDateSelected: (year: Int, month: Int, day: Int) -> Unit) {
    val datePickerState = rememberDatePickerState()

    DatePicker(
        state = datePickerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = millis
            onDateSelected(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
    }
}
