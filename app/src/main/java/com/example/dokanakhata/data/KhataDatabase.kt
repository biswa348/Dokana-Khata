package com.example.dokanakhata.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {

}

@Database(entities = [Customer::class, KhataTransaction::class], version = 1)

abstract class KhataDatabase : RoomDatabase() {
    abstract fun dao(): KhataDao
}