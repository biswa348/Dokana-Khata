package com.example.dokanakhata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dokanakhata.ui.screens.DetailsScreen
import com.example.dokanakhata.ui.screens.Homescreen
import com.example.dokanakhata.ui.screens.LoginScreen
import com.example.dokanakhata.viewmodel.KhataViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: KhataViewModel = viewModel()
            val navController = rememberNavController()
            val shopId by vm.currentShopId.collectAsState(initial = null)

            var isLoading by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                delay(800)
                isLoading = false
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val startDest = if (shopId == null) "login" else "home"
                NavHost(navController = navController, startDestination = startDest) {
                    composable("login") { LoginScreen(navController, vm) }
                    composable("home") { Homescreen(navController, vm) }
                    composable("detail/{customerId}") { backStack ->
                        val id = backStack.arguments?.getString("customerId")?.toIntOrNull() ?: 0
                        DetailsScreen(navController, vm, id)
                    }
                }
            }
        }
    }}