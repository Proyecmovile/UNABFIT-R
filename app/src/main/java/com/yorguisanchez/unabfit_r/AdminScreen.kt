package com.yorguisanchez.unabfit_r

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {

    val context = LocalContext.current
    val activity = context as Activity
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val qrLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val intentResult: IntentResult? =
            IntentIntegrator.parseActivityResult(result.resultCode, result.data)

        if (intentResult?.contents != null) {
            val qrText = intentResult.contents

            val regex = Regex("Fecha: (.*), Hora: (.*)")
            val match = regex.find(qrText)

            if (match != null) {
                val fecha = match.groupValues[1].trim()
                val hora  = match.groupValues[2].trim()

                db.collection("reservaciones")
                    .whereEqualTo("fecha", fecha)
                    .whereEqualTo("hora", hora)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.isEmpty) {
                            Toast.makeText(context, "Reserva no encontrada", Toast.LENGTH_SHORT).show()
                        } else {
                            snapshot.documents.forEach { it.reference.delete() }
                            Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

            } else {
                Toast.makeText(context, "QR no válido", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                actions = {
                    IconButton(
                        onClick = {
                            auth.signOut()
                            navController.navigate("Login") {
                                popUpTo(0)          // limpia el backstack
                            }
                        }
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Escanear QR para eliminar reserva",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val integrator = IntentIntegrator(activity).apply {
                        setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        setPrompt("Enfoca el código QR de la reserva")
                        setBeepEnabled(true)
                        setOrientationLocked(false)
                        captureActivity = com.journeyapps.barcodescanner.CaptureActivity::class.java
                    }
                    qrLauncher.launch(integrator.createScanIntent())
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Escanear QR", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}



