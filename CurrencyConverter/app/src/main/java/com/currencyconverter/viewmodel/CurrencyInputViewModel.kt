package com.currencyconverter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


data class CurrencyInputState(
    var amount: String,
    var currency: Int,
)

class CurrencyInputViewModel() : ViewModel() {
    var label: String = "";
    val state: MutableLiveData<CurrencyInputState> by lazy {
        MutableLiveData<CurrencyInputState>(CurrencyInputState("0", 0));
    }
}