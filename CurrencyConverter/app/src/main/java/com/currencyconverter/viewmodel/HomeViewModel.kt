package com.currencyconverter.viewmodel

import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.currencyconverter.data.api.OpenExchangeApi
import com.currencyconverter.data.cache.CurrencyConverterCache
import com.currencyconverter.data.cache.CurrencyConverterCacheData

class HomeViewModel(private val api: OpenExchangeApi, private val cacheRepo: CurrencyConverterCache): ViewModel() {
    val fromText: MutableLiveData<String> = MutableLiveData(null);
    val toText: MutableLiveData<String> = MutableLiveData(null);
    var timestamp: MutableLiveData<Int> = MutableLiveData(null);

    val fromCurrency: MutableLiveData<Int> = MutableLiveData(null);
    val toCurrency: MutableLiveData<Int> = MutableLiveData(null);
    val error: MutableLiveData<String?> = MutableLiveData(null);

    val allCurrencies: MutableLiveData<Array<Map.Entry<String, String>>> by lazy { MutableLiveData() };
    var rates: Map<String, Double>? = null;
    val networkCallback get() = object: NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.i("CurrencyConverter:HomeViewModel", "Network available");
            if (!isDataAvailable()) fetchLatest();
            error.postValue(null);
        }
        override fun onUnavailable() {
            super.onUnavailable()
            Log.i("CurrencyConverter:HomeViewModel", "Network unavailable");
            error.postValue("No internet, exchange rates won't get updated");
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.i("CurrencyConverter:HomeViewModel", "Network lost");
            error.postValue("No internet, exchange rates won't get updated");
        }
    }

    init {
        if (!loadFromCache()) {
            fetchLatest();
            Log.i("CurrencyConverter:HomeViewModel", "Load latest from OpenExchange");
        } else {
            Log.i("CurrencyConverter:HomeViewModel", "Load data from cache");
        }
    }

    fun isDataAvailable() = this.rates != null && this.allCurrencies.value != null && this.fromCurrency.value != null && this.toCurrency.value != null && this.timestamp.value != null

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

    private fun fetchLatest() {
        api.fetchLatest({_, e ->
            Log.e("CurrencyConverter:HomeViewModel", e.toString());
        }, {_, result ->
            rates = result.rates;
            timestamp.postValue(result.timestamp);
        });
        api.fetchCurrenciesNames({_, e ->
            Log.e("CurrencyConverter:HomeViewModel", e.toString());
        }, {_, result ->
            allCurrencies.postValue(result.entries.toTypedArray());
        });
    }

    private fun convertForward(from: String): String {
        if (from.isBlank()) return "";
        if (allCurrencies.value != null && fromCurrency.value != null && toCurrency.value != null) {
            val fromValue = try { from.toDouble() } catch(e: Exception) { 0.0 };
            val result = fromValue * getConversionRate(allCurrencies.value!![fromCurrency.value!!].key, allCurrencies.value!![toCurrency.value!!].key);
            return result.toBigDecimal().toPlainString();
        }
        return toText.value ?: "";
    }

    private fun convertBackward(to: String): String {
        if (to.isBlank()) return "";
        if (allCurrencies.value != null && fromCurrency.value != null && toCurrency.value != null) {
            val toValue = try { to.toDouble() } catch(e: Exception) { 0.0 };
            val result = toValue * getConversionRate(allCurrencies.value!![toCurrency.value!!].key, allCurrencies.value!![fromCurrency.value!!].key);
            return result.toBigDecimal().toPlainString();
        }
        return fromText.value ?: "";
    }

    private fun getConversionRate(from: String, to: String): Double = (rates?.get(to) ?: 1.0) * 1.0/(rates?.get(from) ?: 1.0)

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
