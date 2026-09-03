package com.example.dokanakhata.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query




@Dao
interface ShopkeeperDao {

    @Insert
    suspend fun register(shopkeeper: Shopkeeper): Long

    @Query("SELECT * FROM Shopkeeper WHERE phone = :phone AND password = :password LIMIT 1")
    suspend fun login(phone: String, password: String): Shopkeeper?

    @Query("SELECT * FROM Shopkeeper WHERE phone = :phone LIMIT 1")
    suspend fun getByPhone(phone: String): Shopkeeper?
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers WHERE shopkeeperId = :shopId ORDER BY id DESC")
    fun getCustomersForShop(shopId: Int): Flow<List<Customer>>

    @Query("SELECT * FROM customers ORDER BY id DESC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Insert
    suspend fun insert(customer: Customer): Long

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Int): Customer?

    @Query("UPDATE customers SET totalBalance = totalBalance + :amount WHERE id = :customerId")
    suspend fun addBalance(customerId: Int, amount: Double)

    @Query("UPDATE customers SET totalBalance = totalBalance - :amount WHERE id = :customerId")
    suspend fun subtractBalance(customerId: Int, amount: Double)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY id DESC")
    fun getTransactions(customerId: Int): Flow<List<KhataTransaction>>

    @Insert
    suspend fun insert(transaction: KhataTransaction)
}