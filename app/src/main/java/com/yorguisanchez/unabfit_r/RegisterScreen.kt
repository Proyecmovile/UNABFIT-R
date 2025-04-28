package com.yorguisanchez.unabfit_r

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.yorguisanchez.unabfit_r.ui.theme.validateEmailRegister
import com.yorguisanchez.unabfit_r.ui.theme.validatepassword

@Composable
fun RegisterScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Logo UNABFIT-R",
                    modifier = Modifier.size(250.dp)
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre Completo") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Nombre")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "Correo")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Contraseña")
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar contraseña") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Confirmar Contraseña")
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val (isEmailValid, emailError) = validateEmailRegister(email)
                        val (isPasswordValid, passwordError) = validatepassword(password)

                        if (fullName.isEmpty()) {
                            Toast.makeText(
                                navController.context,
                                "El nombre completo es requerido",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        if (!isEmailValid) {
                            Toast.makeText(
                                navController.context,
                                emailError,
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        if (!isPasswordValid) {
                            Toast.makeText(
                                navController.context,
                                passwordError,
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        if (password != confirmPassword) {
                            Toast.makeText(
                                navController.context,
                                "Las contraseñas no coinciden",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        // Registro en Firebase
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        navController.context,
                                        "Registro exitoso",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigate("Login") {
                                        popUpTo("Register") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(
                                        navController.context,
                                        "Error en el registro: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Registrarse")
                }
            }
        }
    }
}

