package com.currencyconverter.data.api

import com.currencyconverter.jsonadapter.BigDecimalJsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.math.BigDecimal

data class OpenExchangeLatestResult(
    val disclaimer: String,
    val license: String,
    val timestamp: Long,
    val base: String,
    val rates: Map<String, BigDecimal>
)
// {
//     disclaimer: "https://openexchangerates.org/terms/",
//     license: "https://openexchangerates.org/license/",
//     timestamp: 1449877801,
//     base: "USD",
//     rates: {
//         AED: 3.672538,
//         AFN: 66.809999,
//         ALL: 125.716501,
//         AMD: 484.902502,
//         ANG: 1.788575,
//         AOA: 135.295998,
//         ARS: 9.750101,
//         AUD: 1.390866,
//         /* ... */
//     }
// }
class OpenExchangeApi(val apiKey: String) {
    private val client: OkHttpClient by lazy { OkHttpClient() }
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(BigDecimalJsonAdapter)
            .addLast(KotlinJsonAdapterFactory()).build()
    }

    private fun url(path: String): HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("openexchangerates.org")
            .encodedPath(path)
            .addQueryParameter("app_id", apiKey)
            .build()

    fun fetchCurrenciesNames(
        onFailure: (call: Call?, e: Exception) -> Unit,
        onSuccess: (call: Call, result: Map<String, String>) -> Unit
    ) {
        val jsonAdapter = moshi.adapter<Map<String, String>>(Types.newParameterizedType(Map::class.java, String::class.java, String::class.java));
        val request = Request.Builder()
            .url(url("/api/currencies.json"))
            .build()
        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailure(call, e);
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body!!.string();
                    val result = jsonAdapter.fromJson(body)!!;
                    onSuccess(call, result);
                }
            });
        } catch (ex: Exception) {
            onFailure(null, ex);
        }
    }

    fun fetchLatest(
        onFailure: (call: Call?, e: Exception) -> Unit,
        onSuccess: (call: Call, result: OpenExchangeLatestResult) -> Unit
    ) {
        val jsonAdapter = moshi.adapter(OpenExchangeLatestResult::class.java);
        val request = Request.Builder()
            .url(url("/api/latest.json"))
            .build();
        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailure(call, e);
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body!!.string();
                    val result = jsonAdapter.fromJson(body)!!;
                    onSuccess(call, result);
                }
            });
        } catch (ex: Exception) {
            onFailure(null, ex);
        }
    }
}