package com.currencyconverter.viewmodel

import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.currencyconverter.data.api.OpenExchangeApi

class HomeViewModel(private val api: OpenExchangeApi): ViewModel() {
    val fromText: MutableLiveData<String> = MutableLiveData("");
    val toText: MutableLiveData<String> = MutableLiveData("");

    val fromCurrency: MutableLiveData<Int> = MutableLiveData(0);
    val toCurrency: MutableLiveData<Int> = MutableLiveData(0);

    val allCurrencies: MutableLiveData<Array<Map.Entry<String, String>>> by lazy { MutableLiveData() };
    var rates: Map<String, Double>? = null;

    init {
        api.fetchCurrenciesNames({_, e ->
            throw e
        }, {call, result ->
            allCurrencies.postValue(result.entries.toTypedArray());
        });
        api.fetchLatest({_, e ->
            throw e
        }, {call, result ->
            rates = result.rates;
        });
    }

    fun onFromCurrencySelected(parent: AdapterView<*>, v: View, position: Int, id: Long) = fromUpdated(fromText.value ?: "")

    fun onToCurrencySelected(parent: AdapterView<*>, v: View, position: Int, id: Long) = toUpdated(toText.value ?: "")

    fun fromChanged(editable: Editable?) = fromUpdated(editable.toString())
    private fun fromUpdated(from: String) {
        toText.value = convertForward(from);
        Log.i("HomeViewModel", fromCurrency.value.toString());
    }

    fun toChanged(editable: Editable?) = toUpdated(editable.toString())
    private fun toUpdated(to: String) {
        fromText.value = convertBackward(to);
        Log.i("HomeViewModel", fromCurrency.value.toString());
    }

    private fun convertForward(from: String): String {
        if (from.isBlank()) return "";
        if (allCurrencies.value != null && fromCurrency.value != null && toCurrency.value != null) {
            val fromValue = from.toDouble();
            val result = fromValue * getConversionRate(allCurrencies.value!![fromCurrency.value!!].key, allCurrencies.value!![toCurrency.value!!].key);
            return result.toString();
        }
        return toText.value ?: "";
    }

    private fun convertBackward(to: String): String {
        if (to.isBlank()) return "";
        if (allCurrencies.value != null && fromCurrency.value != null && toCurrency.value != null) {
            val toValue = to.toDouble();
            val result = toValue * getConversionRate(allCurrencies.value!![fromCurrency.value!!].key, allCurrencies.value!![toCurrency.value!!].key);
            return result.toString();
        }
        return fromText.value ?: "";
    }

    private fun getConversionRate(from: String, to: String): Double = (rates?.get(to) ?: 1.0) * 1.0/(rates?.get(from) ?: 1.0)

    class Factory(private val api: OpenExchangeApi): ViewModelProvider.Factory {
        @Override
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass.kotlin) {
                HomeViewModel::class -> modelClass.cast(HomeViewModel(api))!!
                else -> super.create(modelClass)
            }
        }
    }
}
