package com.example.dokanakhata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dokanakhata.data.Customer
import com.example.dokanakhata.data.KhataDatabase
import com.example.dokanakhata.data.KhataTransaction
import com.example.dokanakhata.data.SessionManager
import com.example.dokanakhata.data.Shopkeeper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class KhataViewModel(app: Application) : AndroidViewModel(app) {

    private val db = KhataDatabase.getDatabase(app)
    private val session = SessionManager(app)

    val currentShopId: Flow<Int?> = session.currentShopId
    val currentShopName: Flow<String?> = session.currentShopName

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    // Customers only for this shop
    val customers: StateFlow<List<Customer>> = currentShopId.flatMapLatest { shopId ->
        if (shopId == null) flowOf<List<Customer>>(emptyList())
        else db.customerDao().getCustomersForShop(shopId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTransactions(customerId: Int) = db.transactionDao().getTransactions(customerId)

    fun addCustomer(name: String, phone: String) {
        viewModelScope.launch {
            val shopId = currentShopId.first() ?: return@launch
            val customer = Customer(
                name = name,
                phone = phone,
                shopkeeperId = shopId,
                totalBalance = 0.0
            )
            db.customerDao().insert(customer)
        }
    }

    fun addTransaction(customerId: Int, amount: Double, type: String, note: String) {
        viewModelScope.launch {
            val txn = KhataTransaction(
                customerId = customerId,
                amount = amount,
                type = type,
                note = note,
                date = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                timestamp = System.currentTimeMillis()
            )
            db.transactionDao().insert(txn)

            if (type == "Udhaar") {
                db.customerDao().addBalance(customerId, amount)
            } else {
                db.customerDao().subtractBalance(customerId, amount)
            }
        }
    }

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            val shopkeeper = db.shopkeeperDao().login(phone, password)
            if (shopkeeper != null) {
                session.saveLogin(shopkeeper.id.toInt(), shopkeeper.shopName)
                _loginError.value = null
            } else {
                _loginError.value = "Wrong phone or password"
            }
        }
    }

    fun register(name: String, phone: String, password: String) {
        viewModelScope.launch {
            if (name.isBlank() || phone.isBlank() || password.isBlank()) {
                _loginError.value = "Fill all fields"
                return@launch
            }
            val exists = db.shopkeeperDao().getByPhone(phone)
            if (exists != null) {
                _loginError.value = "Phone already registered"
                return@launch
            }
            val shopkeeper = Shopkeeper(shopName = name, phone = phone, password = password)
            val newId = db.shopkeeperDao().register(shopkeeper).toInt()
            session.saveLogin(newId, name)
            _loginError.value = null
        }
    }

    fun logout(navController: NavController) {
        viewModelScope.launch {
            session.logout()
        }
    }
}