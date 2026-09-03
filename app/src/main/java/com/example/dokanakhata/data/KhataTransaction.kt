package com.example.dokanakhata.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class KhataTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val amount: Double,
    val type: String, // Udhaar or Jama
    val note: String = "",
    val date: String,
    val timestamp: Long = System.currentTimeMillis() // <- THIS LINE MUST BE THERE
)