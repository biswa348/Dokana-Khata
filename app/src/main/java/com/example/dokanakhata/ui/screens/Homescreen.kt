package com.example.dokanakhata.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dokanakhata.viewmodel.KhataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Homescreen(navController: NavController, vm: KhataViewModel) {
    val customers by vm.customers.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dokana Khata") },
                actions = {
                    TextButton(onClick = { vm.logout(navController) }) {
                        Text("Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(customers) { customer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { navController.navigate("detail/${customer.id}") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(customer.name, style = MaterialTheme.typography.titleMedium)
                        Text(customer.phone, style = MaterialTheme.typography.bodySmall)
                        Text("Balance: ₹${customer.totalBalance}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Customer") },
                text = {
                    Column {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (name.isNotBlank()) {
                            vm.addCustomer(name, phone)
                            name = ""; phone = ""; showDialog = false
                        }
                    }) { Text("Add") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}