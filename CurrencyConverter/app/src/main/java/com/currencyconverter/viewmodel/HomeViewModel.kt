package com.currencyconverter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class CurrencyExchange(
    val amount: String,
    val currency: Int,
)

class HomeViewModel(): ViewModel() {
    val from: MutableLiveData<CurrencyExchange> by lazy {
        MutableLiveData<CurrencyExchange>(CurrencyExchange(amount = "0", currency = 0))
    }
    val to: MutableLiveData<CurrencyExchange> by lazy {
        MutableLiveData<CurrencyExchange>(CurrencyExchange(amount = "0", currency = 0))
    }

    fun convertForward(from: String): String {
        if (from.isBlank()) return "";
        return from;
    }

    fun convertBackward(to: String): String {
        if (to.isBlank()) return "";
        return to;
    }

    fun currencyList(): Array<CharSequence> {
        return arrayOf("Hello world", "Hi", "How are you doing", "Good right");
    }
}
