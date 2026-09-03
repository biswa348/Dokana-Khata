package com.example.dokanakhata.ui.screens
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dokanakhata.viewmodel.KhataViewModel

@Composable
fun LoginScreen(navController: NavController, vm: KhataViewModel) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    val error by vm.loginError.collectAsState()
    val currentShopId by vm.currentShopId.collectAsState(initial = null)

    // Auto navigate if already logged in
    LaunchedEffect(currentShopId) {
        if (currentShopId != null) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // Navigate after login/register success
    LaunchedEffect(error) {
        if (error == null && currentShopId != null) {
            // handled above
        }
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Dokana Khata", style = MaterialTheme.typography.headlineLarge)
        Text(
            if (isRegister) "Register your Shop" else "Shopkeeper Login",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(24.dp))
        if (isRegister) {
            OutlinedTextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = { Text("Shop Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
        }
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                if (isRegister) {
                    vm.register(shopName, phone, password)
                } else {
                    vm.login(phone, password)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(if (isRegister) "Register & Login" else "Login")
        }
        Spacer(Modifier.height(12.dp))
        TextButton(
            onClick = { isRegister = !isRegister },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRegister) "Already have account? Login" else "New shop? Create Account")
        }
    }
}