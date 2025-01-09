package com.currencyconverter.viewmodel

import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.currencyconverter.data.api.OpenExchangeApi
import com.currencyconverter.data.cache.CurrencyConverterCache
import com.currencyconverter.data.cache.CurrencyConverterCacheData
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeViewModel(private val api: OpenExchangeApi, private val cacheRepo: CurrencyConverterCache): ViewModel() {
    enum class Error { NO_INTERNET, FETCH_DATA }

    private val TAG = "CurrencyConverter:HomeViewModel";
    private val fetchInterval: Long = 1000 * 20;
    private var hasNetwork = false;
    private val precision = 10;
    private val ONE = BigDecimal.ONE.setScale(precision, RoundingMode.HALF_EVEN);

    val fromText: MutableLiveData<String> = MutableLiveData(null);
    val toText: MutableLiveData<String> = MutableLiveData(null);
    var timestamp: MutableLiveData<Long> = MutableLiveData(null);
    val handler = android.os.Handler(Looper.myLooper()!!);
    val isLoading = MutableLiveData(false);
    val timestampString: LiveData<String> = timestamp.map {
        if (it == null) return@map "";
        Log.i(TAG, it.toString());
        return@map DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(it));
    }

    val fromCurrency: MutableLiveData<Int> = MutableLiveData(null);
    val toCurrency: MutableLiveData<Int> = MutableLiveData(null);
    val error: MutableLiveData<Error?> = MutableLiveData(Error.NO_INTERNET);

    val allCurrencies: MutableLiveData<Array<Map.Entry<String, String>>> by lazy { MutableLiveData() };
    var rates: Map<String, BigDecimal>? = null;
    val networkCallback = object: NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.i(TAG, "Network available!!!");
            Log.i(TAG, "Has data " + isDataAvailable().toString());
            hasNetwork = true;
            error.postValue(null);
            if (!isDataAvailable()) fetchLatest();
            handler.postDelayed(object: Runnable {
                override fun run() {
                    Log.i(TAG, "CHECK MODEL");
                    fetchLatest()
                    handler.postDelayed(this, fetchInterval);
                }
            }, fetchInterval);
        }
        override fun onUnavailable() {
            super.onUnavailable()
            Log.i(TAG, "Network unavailable");
            hasNetwork = false;
            error.postValue(Error.NO_INTERNET);
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.i(TAG, "Network lost");
            hasNetwork = false;
            handler.removeCallbacksAndMessages(null);
            error.postValue(Error.NO_INTERNET);
        }
    }

    init {
        if (!loadFromCache()) {
            fetchLatest();
            Log.i(TAG, "Load latest from OpenExchange");
        } else {
            Log.i(TAG, "Load data from cache");
        }
    }

    fun isDataAvailable() = this.rates != null && this.allCurrencies.value != null;

    fun refreshClick(view: View) {
        fetchLatest();
    }

    fun saveToCache(): Boolean {
        if (isDataAvailable()) {
            cacheRepo.saveCache(CurrencyConverterCacheData(
                rates = this.rates!!,
                names = this.allCurrencies.value!!,
                timestamp = this.timestamp.value!!,
                fromCurrency = this.fromCurrency.value!!,
                toCurrency =  this.toCurrency.value!!
            ))
            return true;
        }
        return false;
    }

    fun loadFromCache(): Boolean {
        val data = cacheRepo.loadData();
        if (data != null) {
            this.rates = data.rates;
            this.allCurrencies.value = data.names;
            this.fromCurrency.value = data.fromCurrency;
            this.toCurrency.value = data.toCurrency;
            this.timestamp.value = data.timestamp;
            return true;
        }
        return false;
    }

    fun fromUpdated(from: String) {
        toText.value = convertForward(from);
    }

    fun toUpdated(to: String) {
        fromText.value = convertBackward(to);
    }

    fun fetchLatest() {
        if (!hasNetwork || isLoading.value == true) return;
        Log.i(TAG, "Fetch Data");
        isLoading.postValue(true);

        // NOTE: this can be done in parallel but just do it this way so it takes more time
        api.fetchLatest({_, e ->
            Log.e(TAG, e.toString());
            error.postValue(Error.FETCH_DATA);
            isLoading.postValue(false);
            hasNetwork = false;
        }, {_, result ->
            rates = result.rates;
            timestamp.postValue(Instant.now().epochSecond);
            api.fetchCurrenciesNames({_, e ->
                Log.e(TAG, e.toString());
                error.postValue(Error.FETCH_DATA);
                isLoading.postValue(false);
                hasNetwork = false;
            }, {_, res ->
                isLoading.postValue(false);
                allCurrencies.postValue(res.entries.toTypedArray());
            });
        });
    }

    private fun convertForward(from: String): String {
        if (from.isBlank()) return "";
        if (allCurrencies.value != null && fromCurrency.value != null && toCurrency.value != null) {
            val fromValue = try { from.toBigDecimal().setScale(precision, RoundingMode.HALF_EVEN) } catch(e: Exception) { Log.e(TAG, e.toString()); BigDecimal.ZERO };
            val result = fromValue * getConversionRate(allCurrencies.value!![fromCurrency.value!!].key, allCurrencies.value!![toCurrency.value!!].key);
            return result.setScale(precision, RoundingMode.HALF_EVEN).toString();
        }
        return toText.value ?: "";
    }

    private fun convertBackward(to: String): String {
        if (to.isBlank()) return "";
        if (allCurrencies.value != null && fromCurrency.value != null && toCurrency.value != null) {
            val toValue = try { to.toBigDecimal().setScale(precision, RoundingMode.HALF_EVEN) } catch(e: Exception) { Log.e(TAG, e.toString()); 0.toBigDecimal() };
            val result = toValue * getConversionRate(allCurrencies.value!![toCurrency.value!!].key, allCurrencies.value!![fromCurrency.value!!].key);
            return result.setScale(precision, RoundingMode.HALF_EVEN).toString();
        }
        return fromText.value ?: "";
    }

    private fun getConversionRate(from: String, to: String): BigDecimal {
        if (rates?.containsKey(to) != true) Log.e(TAG, "$to conversion rate not found");
        if (rates?.containsKey(from) != true) Log.e(TAG, "$from conversion rate not found");
        return (rates?.get(to) ?: ONE) * ONE / (rates?.get(from) ?: ONE);
    }

    class Factory(
        private val api: OpenExchangeApi,
        private val cacheRepo: CurrencyConverterCache
    ): ViewModelProvider.Factory {
        @Override
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass.kotlin) {
                HomeViewModel::class -> modelClass.cast(HomeViewModel(api, cacheRepo))!!
                else -> super.create(modelClass)
            }
        }
    }
}
