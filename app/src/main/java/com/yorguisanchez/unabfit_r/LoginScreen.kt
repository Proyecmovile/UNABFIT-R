package com.yorguisanchez.unabfit_r

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.yorguisanchez.unabfit_r.ui.theme.validatepassword
import com.yorguisanchez.unabfit_r.ui.theme.validationEmail

@Composable
fun LoginScreen(navController: NavController) {
    Scaffold { innerpadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
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
                    modifier = Modifier.size(300.dp)
                )

                var inputEmail by remember { mutableStateOf("") }
                var inputPassword by remember { mutableStateOf("") }

                val activity = LocalView.current.context as Activity


                OutlinedTextField(
                    value = inputEmail,
                    onValueChange = { inputEmail = it },
                    label = { Text("Correo Electrónico") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Correo"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = inputPassword,
                    onValueChange = { inputPassword = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Contraseña"
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {

                        val (isEmailValid, emailError) = validationEmail(inputEmail)
                        val (isPasswordValid, passwordError) = validatepassword(inputPassword)

                        if (!isEmailValid) {
                            Toast.makeText(
                                activity.applicationContext,
                                emailError,
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        if (!isPasswordValid) {
                            Toast.makeText(
                                activity.applicationContext,
                                passwordError,
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        val auth = Firebase.auth

                        auth.signInWithEmailAndPassword(inputEmail, inputPassword)
                            .addOnCompleteListener(activity) { task ->

                                if (task.isSuccessful) {
                                    navController.navigate("Home")
                                } else {
                                    Toast.makeText(
                                        activity.applicationContext,
                                        "Error en credenciales",
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
                    Text("Iniciar sesión")
                }

                TextButton(onClick = {
                    navController.navigate("Register")

                }) {
                    Text(
                        text = "¿No tienes cuenta? Regístrate",
                        color = Color(0xFFFF9800),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
