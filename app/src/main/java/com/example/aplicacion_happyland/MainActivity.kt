package com.example.aplicacion_happyland

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicacion_happyland.screens.AddCardScreen
import com.example.aplicacion_happyland.screens.BirthdayReservationScreen
import com.example.aplicacion_happyland.screens.HomeScreen
import com.example.aplicacion_happyland.screens.LoginScreen
import com.example.aplicacion_happyland.screens.RegisterScreen
import com.example.aplicacion_happyland.screens.WelcomeScreen
import com.example.aplicacion_happyland.ui.theme.Aplicacion_HappylandTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Aplicacion_HappylandTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("addcard") { AddCardScreen(navController) }
        composable("birthdayReservation") { BirthdayReservationScreen(navController) }
    }
}
