package com.example.dokanakhata.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("session")

class SessionManager(private val context: Context) {

    companion object {
        val SHOP_ID_KEY = intPreferencesKey("shop_id")
        val SHOP_NAME_KEY = stringPreferencesKey("shop_name")
    }

    val currentShopId: Flow<Int?> = context.dataStore.data.map { it[SHOP_ID_KEY] }
    val currentShopName: Flow<String?> = context.dataStore.data.map { it[SHOP_NAME_KEY] }

    suspend fun saveLogin(shopId: Int, shopName: String) {
        context.dataStore.edit { pref ->
            pref[SHOP_ID_KEY] = shopId
            pref[SHOP_NAME_KEY] = shopName
        }
    }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }
}