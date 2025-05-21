package com.yorguisanchez.unabfit_r

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import android.graphics.Bitmap
import androidx.compose.material.icons.filled.AccountCircle
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

data class Reserva(
    val id: String = "",
    val fecha: String = "",
    val hora: String = ""
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(navController: NavController) {

    val calendarColor = Color(0xFFBDBDBD)

    var reservas by remember { mutableStateOf(listOf<Reserva>()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedReserva by remember { mutableStateOf<Reserva?>(null) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email

    LaunchedEffect(userEmail) {
        if (userEmail == null) {
            isLoading = false
            return@LaunchedEffect
        }

        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collectionGroup("reservaciones")
                .whereEqualTo("usuarioEmail", userEmail)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            reservas = snapshot.documents.mapNotNull { doc ->
                Reserva(
                    id = doc.id,
                    fecha = doc.getString("fecha") ?: return@mapNotNull null,
                    hora = doc.getString("hora") ?: return@mapNotNull null
                )
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            reservas = emptyList()
        } finally {
            isLoading = false
        }
    }

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
                        onClick = { /* Menú */ },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) { Icon(Icons.Default.Menu, contentDescription = "Menú") }
                    IconButton(
                        onClick = { /* Perfil */ },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) { Icon(Icons.Default.AccountCircle, contentDescription = "Perfil") }
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
                    IconButton(onClick = { navController.navigate("Reservation") }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                    }
                    IconButton(onClick = {  }) {
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

            Text(
                text = "Mis Reservas",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                userEmail == null -> {
                    Text(
                        text = "No hay sesión iniciada.",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
                reservas.isEmpty() -> {
                    Text(
                        text = "No tienes reservas registradas.",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    LazyColumn {
                        items(reservas) { reserva ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = calendarColor),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Fecha: ${reserva.fecha}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Hora:  ${reserva.hora}",
                                            fontSize = 16.sp
                                        )
                                    }
                                    Button(onClick = {
                                        selectedReserva = reserva
                                    }) {
                                        Text("Ver QR")
                                    }
                                }
                            }
                        }
                    }
                    if (selectedReserva != null) {
                        AlertDialog(
                            onDismissRequest = { selectedReserva = null },
                            confirmButton = {
                                TextButton(onClick = { selectedReserva = null }) {
                                    Text("Cerrar")
                                }
                            },
                            title = {
                                Text("Código QR de la Reserva")
                            },
                            text = {
                                val qrData = "Reserva - Fecha: ${selectedReserva!!.fecha}, Hora: ${selectedReserva!!.hora}"
                                val qrBitmap = generateQrCodeBitmap(qrData)
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "QR Code",
                                    modifier = Modifier.size(250.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

fun generateQrCodeBitmap(data: String): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bitmap
}