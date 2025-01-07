package com.currencyconverter.viewmodel

import androidx.lifecycle.ViewModel

class HomeViewModel(): ViewModel() {

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
