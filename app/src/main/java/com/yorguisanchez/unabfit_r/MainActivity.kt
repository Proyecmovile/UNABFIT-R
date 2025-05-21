package com.yorguisanchez.unabfit_r

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.yorguisanchez.unabfit_r.ui.theme.UNABFITRTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UNABFITRTheme {
                val navController = rememberNavController()

                val user = FirebaseAuth.getInstance().currentUser
                val userEmail = user?.email

                val startDestination = when {
                    userEmail == "admin@unab.edu.co" -> "Admin"
                    userEmail != null -> "Home"
                    else -> "Login"
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable(route = "Login") {
                        LoginScreen(navController)
                    }
                    composable(route = "Register") {
                        RegisterScreen(navController)
                    }
                    composable(route = "Home") {
                        HomeScreen(navController)
                    }
                    composable(route = "Admin") {
                        AdminScreen(navController)
                    }
                    composable(route = "Reservation") {
                        ReservationsScreen(navController)
                    }
                    composable(route = "Reservations") {
                        MyReservationsScreen(navController)
                    }
                }
            }
        }
    }
}
