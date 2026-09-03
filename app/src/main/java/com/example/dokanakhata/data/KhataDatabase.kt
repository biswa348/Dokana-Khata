package com.example.dokanakhata.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Shopkeeper::class, Customer::class, KhataTransaction::class], version = 3, exportSchema = false)
abstract class KhataDatabase : RoomDatabase() {
    abstract fun shopkeeperDao(): ShopkeeperDao
    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile private var INSTANCE: KhataDatabase? = null
        fun getDatabase(context: Context): KhataDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KhataDatabase::class.java,
                    "dokana_khata_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}