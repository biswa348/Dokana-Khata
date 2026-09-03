package com.example.dokanakhata.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "transactions")
data class KhataTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val amount: Int,
    val isUdhaar: Boolean,
    val note: String,
    val time: Long = System.currentTimeMillis()
)