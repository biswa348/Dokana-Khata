package com.example.dokanakhata.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KhataDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun getCustomerById(id: Long): Flow<Customer?>

    @Query("SELECT * FROM transactions WHERE customerId = :cid ORDER BY time DESC")
    fun getTransactions(cid: Long): Flow<List<KhataTransaction>>

    @Query("SELECT * FROM transactions WHERE customerId = :cid ORDER BY time DESC")
    fun getTransactionsForCustomer(cid: Long): Flow<List<KhataTransaction>>

    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): Flow<List<KhataTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(c: Customer): Long

    @Insert
    suspend fun insertTransaction(t: KhataTransaction)

    @Delete
    suspend fun deleteTransaction(t: KhataTransaction)
}