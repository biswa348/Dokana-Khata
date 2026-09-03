package com.example.dokanakhata.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Shopkeeper(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val shopName: String,
    val phone: String,
    val password: String
)