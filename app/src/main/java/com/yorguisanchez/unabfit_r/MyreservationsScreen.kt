package com.yorguisanchez.unabfit_r

// ---------- IMPORTS ----------
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

// ---------- DATA CLASS ----------
data class Reserva(
    val id: String = "",      // doc ID (opcional, p/QR o eliminar)
    val fecha: String = "",
    val hora: String = ""
)

// ---------- MAIN COMPOSABLE ----------
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(navController: NavController) {

    // --- UI color base ---
    val calendarColor = Color(0xFFBDBDBD)

    // --- State ---
    var reservas by remember { mutableStateOf(listOf<Reserva>()) }
    var isLoading by remember { mutableStateOf(true) }

    // --- Firebase Auth ---
    val userEmail = FirebaseAuth.getInstance().currentUser?.email

    // --- Cargar reservas del usuario al abrir pantalla ---
    LaunchedEffect(userEmail) {
        if (userEmail != null) {
            val db = FirebaseFirestore.getInstance()
            try {
                val snapshot = db.collection("reservaciones")
                    .whereEqualTo("usuarioEmail", userEmail)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                reservas = snapshot.documents.mapNotNull { doc ->
                    val fecha = doc.getString("fecha") ?: return@mapNotNull null
                    val hora  = doc.getString("hora")  ?: return@mapNotNull null
                    Reserva(doc.id, fecha, hora)
                }
            } catch (e: Exception) {
                reservas = emptyList()
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    // ---------- UI ----------
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color(0xFF424242),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = { /* Menú */ },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                    IconButton(
                        onClick = { /* Perfil */ },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
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
                    IconButton(onClick = { /* Favoritos */ }) {
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

            // ---------- Encabezado ----------
            Text(
                text = "Mis Reservas",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // ---------- Contenido ----------
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
                                    // ---------- Datos reserva (izquierda) ----------
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
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

                                    // ---------- Botón Ver QR (derecha) ----------
                                    Button(
                                        onClick = {

                                        }
                                    ) {
                                        Text("Ver QR")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

