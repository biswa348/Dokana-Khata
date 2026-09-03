package com.example.dokanakhata.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dokanakhata.data.Customer
import com.example.dokanakhata.data.KhataTransaction
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailsScreen(
    customer: Customer,
    transactions: List<KhataTransaction>,
    onAddTxn: (Int, Boolean, String) -> Unit,
    onDelete: (KhataTransaction) -> Unit
) {
    var showAdd by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isUdhaar by remember { mutableStateOf(true) }
    val df = SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault())

    val balance = transactions.sumOf { if (it.isUdhaar) it.amount else -it.amount }

    Column(Modifier.padding(16.dp)) {
        Text("Customer: ${customer.name}", style = MaterialTheme.typography.headlineSmall)
        Text("Balance: Rs. $balance", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { showAdd = true }) { Text("Add Transaction") }

        LazyColumn {
            items(transactions) { txn ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${if (txn.isUdhaar) "Udhar" else "Jama"}: Rs. ${txn.amount}")
                        Text(txn.note)
                        Text(df.format(Date(txn.time)), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        if (showAdd) {
            AlertDialog(
                onDismissRequest = { showAdd = false },
                title = { Text("Add Transaction") },
                text = {
                    Column {
                        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })
                        OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") })
                        Row {
                            RadioButton(selected = isUdhaar, onClick = { isUdhaar = true })
                            Text("Udhaar")
                            RadioButton(selected = !isUdhaar, onClick = { isUdhaar = false })
                            Text("Jama")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val amt = amount.toIntOrNull() ?: 0
                        if (amt > 0) {
                            onAddTxn(amt, isUdhaar, note)
                            amount = ""; note = ""; showAdd = false
                        }
                    }) { Text("Add") }
                },
                dismissButton = {
                    TextButton(onClick = { showAdd = false }) { Text("Cancel") }
                }
            )
        }
    }
}