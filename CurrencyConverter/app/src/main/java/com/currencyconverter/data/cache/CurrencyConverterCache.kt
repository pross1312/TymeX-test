package com.currencyconverter.data.cache

import android.util.Log
import com.currencyconverter.jsonadapter.BigDecimalJsonAdapter
import com.currencyconverter.jsonadapter.MapEntryJsonAdapter
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.math.BigDecimal
import java.util.AbstractMap.SimpleEntry

data class CurrencyConverterCacheData(
    val rates: Map<String, BigDecimal>,
    val names: Array<Map.Entry<String, String>>,
    val timestamp: Long,
    val fromCurrency: Int,
    val toCurrency: Int
)

class CurrencyConverterCache(private val cacheFile: File) {
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(MapEntryJsonAdapter)
            .add(BigDecimalJsonAdapter)
            .addLast(KotlinJsonAdapterFactory()).build()
    }

    fun saveCache(data: CurrencyConverterCacheData): Boolean {
        val jsonAdapter = moshi.adapter(CurrencyConverterCacheData::class.java);
        try {
            val json = jsonAdapter.toJson(data);
            cacheFile.createNewFile();
            cacheFile.writeText(json);
            return true;
        } catch (ex: Exception) {
            Log.e("CurrencyConverter:Cache", ex.toString());
        }
        return false;
    }

    fun loadData(): CurrencyConverterCacheData? {
        val jsonAdapter = moshi.adapter(CurrencyConverterCacheData::class.java);
        try {
            val result = jsonAdapter.fromJson(cacheFile.readText());
            return result;
        } catch (ex: Exception) {
            Log.e("CurrencyConverter:Cache", ex.toString());
        }
        return null;
    }
}