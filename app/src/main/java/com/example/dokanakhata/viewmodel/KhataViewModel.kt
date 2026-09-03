package com.example.dokanakhata.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dokanakhata.data.Customer
import com.example.dokanakhata.data.KhataDatabase
import com.example.dokanakhata.data.KhataTransaction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class KhataViewModel(private val db: KhataDatabase) : ViewModel() {

    val customers: StateFlow<List<Customer>> = db.dao().getAllCustomers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCustomerId = MutableStateFlow<Long?>(null)

    val transactions: StateFlow<List<KhataTransaction>> = _selectedCustomerId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else db.dao().getTransactionsForCustomer(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCustomer(id: Long) {
        _selectedCustomerId.value = id
    }

    fun addCustomer(name: String, phone: String) {
        viewModelScope.launch {
            db.dao().insertCustomer(Customer(name = name, phone = phone))
        }
    }

    fun addTransaction(amount: Int, isUdhaar: Boolean, note: String) {
        val customerId = _selectedCustomerId.value ?: return
        viewModelScope.launch {
            db.dao().insertTransaction(
                KhataTransaction(
                    customerId = customerId,
                    amount = amount,
                    isUdhaar = isUdhaar,
                    note = note,
                    time = System.currentTimeMillis()
                )
            )
        }
    }

    fun getCustomerById(id: Long): Flow<Customer?> {
        return db.dao().getCustomerById(id)
    }
}