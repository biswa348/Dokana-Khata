package com.example.dokanakhata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.dokanakhata.data.KhataDatabase
import com.example.dokanakhata.viewmodel.KhataViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            KhataDatabase::class.java, "dokana-khata.db"
        ).fallbackToDestructiveMigration()
            .build()

        val viewModel = KhataViewModel(db)

        setContent {
            MaterialTheme {
                AppNav(viewModel)
            }
        }
    }
}

@Composable
fun AppNav(viewModel: KhataViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            CustomerListScreen(viewModel, navController)
        }
        composable(
            "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            CustomerDetailScreen(viewModel, id, navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(viewModel: KhataViewModel, navController: androidx.navigation.NavController) {
    val customers by viewModel.customers.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dokana Khata") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) { Text("+") }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            if (showAdd) {
                Card(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Customer Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Button(onClick = {
                                if (name.isNotBlank()) {
                                    viewModel.addCustomer(name, phone)
                                    name = ""; phone = ""; showAdd = false
                                }
                            }) { Text("Add") }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = { showAdd = false }) { Text("Cancel") }
                        }
                    }
                }
            }

            if (customers.isEmpty()) {
                Text("No customers yet. Add first customer.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn {
                    items(customers) { customer ->
                        Card(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.selectCustomer(customer.id)
                                    navController.navigate("detail/${customer.id}")
                                }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(customer.name, style = MaterialTheme.typography.titleMedium)
                                Text(customer.phone, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(viewModel: KhataViewModel, customerId: Long, navController: androidx.navigation.NavController) {
    val customer by viewModel.getCustomerById(customerId).collectAsState(initial = null)
    val transactions by viewModel.transactions.collectAsState()
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isUdhaar by remember { mutableStateOf(true) }

    LaunchedEffect(customerId) { viewModel.selectCustomer(customerId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Details") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Phone: ${customer?.phone ?: ""}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))

            // Add Transaction
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount ₹") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    Row(Modifier.padding(top = 8.dp)) {
                        FilterChip(selected = isUdhaar, onClick = { isUdhaar = true }, label = { Text("Udhaar") })
                        Spacer(Modifier.width(8.dp))
                        FilterChip(selected = !isUdhaar, onClick = { isUdhaar = false }, label = { Text("Jama") })
                    }
                    Button(
                        modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                        onClick = {
                            val amt = amount.toIntOrNull() ?: 0
                            if (amt > 0) {
                                viewModel.addTransaction(amt, isUdhaar, note)
                                amount = ""; note = ""
                            }
                        }
                    ) { Text("Add Transaction") }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("History:", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(transactions) { t ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(if (t.isUdhaar) "Udhaar - ₹${t.amount}" else "Jama - ₹${t.amount}", color = if (t.isUdhaar) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                                Text(t.note, style = MaterialTheme.typography.bodySmall)
                                Text(formatTime(t.time), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}