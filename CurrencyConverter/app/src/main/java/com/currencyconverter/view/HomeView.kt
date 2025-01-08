package com.currencyconverter.view

import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.View.OnFocusChangeListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.currencyconverter.data.api.OpenExchangeApi
import com.currencyconverter.data.cache.CurrencyConverterCache
import com.currencyconverter.databinding.ActivityHomeBinding
import com.currencyconverter.view.adapter.CurrencySpinnerAdapter
import com.currencyconverter.viewmodel.HomeViewModel
import java.io.File


class HomeView: AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding;
    private val api by lazy { OpenExchangeApi("902e8d27de3f412f96039e606b769039") }
    private val cacheRepo by lazy { CurrencyConverterCache(File(cacheDir, "currencyConverter")) }
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory(api, cacheRepo) };

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater);
        val adapter = CurrencySpinnerAdapter(this, viewModel.allCurrencies);
        binding.viewModel = viewModel;
        binding.spinnerFrom.adapter = adapter;
        binding.spinnerTo.adapter = adapter;
        binding.lifecycleOwner = this;
        bindObserver();
        registerNetworkCallback();
        setContentView(binding.root);
    }

    override fun onStop() {
        Log.i("CurrencyConverter:HomePage", "OnStop " + viewModel.saveToCache().toString());
        super.onStop()
    }

    private fun registerNetworkCallback() {
        try {
            val connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerDefaultNetworkCallback(viewModel.networkCallback);
        } catch (e: Exception) {
            throw e;
        }
    }

    private fun bindObserver() {
        binding.spinnerFrom.isFocusableInTouchMode = true;
        binding.spinnerFrom.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (binding.spinnerFrom.windowToken != null) {
                    binding.spinnerFrom.performClick()
                }
            }
        }
        binding.spinnerTo.isFocusableInTouchMode = true;
        binding.spinnerTo.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (binding.spinnerTo.windowToken != null) {
                    binding.spinnerTo.performClick()
                }
            }
        }
        viewModel.toText.observe(this) { text ->
            if (binding.textTo.hasFocus() || binding.spinnerTo.hasFocus()) {
                viewModel.toUpdated(text);
            }
        }
        viewModel.fromText.observe(this) { text ->
            if (binding.textFrom.hasFocus() || binding.spinnerFrom.hasFocus()) {
                viewModel.fromUpdated(text);
            }
        }
        viewModel.fromCurrency.observe(this) { _ ->
            viewModel.fromUpdated(viewModel.fromText.value ?: "");
        }
        viewModel.toCurrency.observe(this) { _ ->
            viewModel.toUpdated(viewModel.toText.value ?: "");
        }
    }
}