package com.example.dokanakhata.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "customers",
    foreignKeys = [ForeignKey(
        entity = Shopkeeper::class,
        parentColumns = ["id"],
        childColumns = ["shopkeeperId"],
        onDelete = ForeignKey.CASCADE

    )]
)
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val shopkeeperId: Int,
    val name: String,
    val phone: String,
    val totalBalance: Double = 0.0,
    val shopId: String = ""
)