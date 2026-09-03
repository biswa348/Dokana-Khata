package com.example.dokanakhata.ui.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dokanakhata.data.Customer
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    customers: List<Customer>, balances: Map<Long, Double>,
    totalUdhar: Double, totalJama: Double,
    onCustomerClick: (Customer) -> Unit,
    onAddCustomer: (String, String) -> Unit,
    onSeed: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    val nf = NumberFormat.getCurrencyInstance(Locale("en","IN"))
    val filtered = customers.filter { it.name.contains(search, true) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dokana Khata", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1B4B), titleContentColor = Color.White)) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }, containerColor = Color(0xFFF59E0B)) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("Udhar Deba", style = MaterialTheme.typography.labelSmall); Text(nf.format(totalUdhar), color = Color(0xFFDC2626), fontWeight = FontWeight.Bold) }
                    Column { Text("Jama Neba", style = MaterialTheme.typography.labelSmall); Text(nf.format(totalJama), color = Color(0xFF16A34A), fontWeight = FontWeight.Bold) }
                    Column { Text("Mota Baki", style = MaterialTheme.typography.labelSmall); Text(nf.format(totalUdhar - totalJama), fontWeight = FontWeight.Bold) }
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = search, onValueChange = { search = it }, placeholder = { Text("Grahaka khoja...") },
                leadingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.fillMaxWidth())
            if(customers.isEmpty()){ Button(onClick = onSeed, modifier = Modifier.padding(top=8.dp)) { Text("Odia Demo Grahaka Add Kara") } }
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { c ->
                    val bal = c.totalBalance
                    Card(Modifier.fillMaxWidth().clickable { onCustomerClick(c) }) {
                        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text(c.name, fontWeight = FontWeight.SemiBold); Text(c.phone, style = MaterialTheme.typography.bodySmall) }
                            Text((if(bal>=0) "Paibaku Achhi " else "Debaku Achhi ") + nf.format(kotlin.math.abs(bal)),
                                color = if(bal>=0) Color(0xFFDC2626) else Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
    if(showAdd){
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showAdd=false }, title = { Text("Nua Grahaka Joda") },
            text = { Column { OutlinedTextField(name, {name=it}, label={Text("Naam *")}); Spacer(Modifier.height(8.dp)); OutlinedTextField(phone, {phone=it}, label={Text("Phone / Gaon")}) } },
            confirmButton = { Button(onClick = { if(name.isNotBlank()){ onAddCustomer(name,phone); showAdd=false } }) { Text("Joda") } },
            dismissButton = { TextButton(onClick={showAdd=false}){ Text("Cancel") } }
        )
    }
}