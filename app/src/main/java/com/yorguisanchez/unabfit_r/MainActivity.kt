package com.yorguisanchez.unabfit_r

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yorguisanchez.unabfit_r.ui.theme.UNABFITRTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNABFITRTheme {

                val myNavController = rememberNavController()
                val myStartDestination: String = "Login"

                NavHost(
                    navController = myNavController,
                    startDestination = myStartDestination
                ) {
                    composable(route = "Login") {
                        LoginScreen(myNavController)
                    }
                    composable(route = "Register") {
                        RegisterScreen()
                    }
                }
            }
        }
    }
}
