package com.example.dokanakhata.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dokanakhata.viewmodel.KhataViewModel
import java.text.SimpleDateFormat
import java.util.*

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavController, vm: KhataViewModel, customerId: Int) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Udhaar") }
    val transactions by vm.getTransactions(customerId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Details") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount ₹") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    Row {
                        FilterChip(selected = type == "Udhaar", onClick = { type = "Udhaar" }, label = { Text("Udhaar") })
                        Spacer(Modifier.width(8.dp))
                        FilterChip(selected = type == "Jama", onClick = { type = "Jama" }, label = { Text("Jama") })
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = {
                        val amt = amount.toDoubleOrNull()
                        if (amt != null) {
                            vm.addTransaction(customerId, amt, type, note)
                            amount = ""; note = ""
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Add Transaction")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("History:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn {
                items(transactions) { t ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("${t.type} - ₹${t.amount}", color = if (t.type == "Udhaar") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                            Text(t.date, style = MaterialTheme.typography.bodySmall)
                            if (t.note.isNotBlank()) Text(t.note, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}