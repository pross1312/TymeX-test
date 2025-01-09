package com.currencyconverter.view

import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.OnFocusChangeListener
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.currencyconverter.R
import com.currencyconverter.data.api.OpenExchangeApi
import com.currencyconverter.data.cache.CurrencyConverterCache
import com.currencyconverter.databinding.ActivityHomeBinding
import com.currencyconverter.view.adapter.CurrencySpinnerAdapter
import com.currencyconverter.viewmodel.HomeViewModel
import java.io.File


class HomeView: AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding;
    private val TAG = "CurrencyConverter:HomeView"
    private val api by lazy { OpenExchangeApi("902e8d27de3f412f96039e606b769039") }
    private val cacheRepo by lazy { CurrencyConverterCache(File(cacheDir, "currencyConverter")) }
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory(api, cacheRepo) };
    private val handler: Handler = Handler(Looper.myLooper()!!);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater);
        val adapter = CurrencySpinnerAdapter(this, viewModel.allCurrencies);
        binding.viewModel = viewModel;
        binding.spinnerFrom.adapter = adapter;
        binding.spinnerTo.adapter = adapter;
        binding.themeButton.setOnClickListener {
            unbindObserver()
            if (resources.getString(R.string.mode) == "light") {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            bindObserver();
        }
        binding.spinnerFrom.isFocusableInTouchMode = true;
        binding.spinnerFrom.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (binding.spinnerFrom.windowToken != null) {
                    binding.spinnerFrom.performClick()
                }
            }
        }
        binding.spinnerTo.isFocusableInTouchMode = true;
        binding.spinnerTo.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (binding.spinnerTo.windowToken != null) {
                    binding.spinnerTo.performClick()
                }
            }
        }
        binding.lifecycleOwner = this;
        bindObserver();
        setContentView(binding.root);
    }

    override fun onResume() {
        super.onResume()
        registerNetworkCallback();
    }

    override fun onPause() {
        unregisterNetworkCallback();
        super.onPause()
    }

    override fun onStop() {
        Log.i(TAG, "OnStop " + viewModel.saveToCache().toString());
        super.onStop()
    }

    private fun registerNetworkCallback() {
        try {
            val connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerDefaultNetworkCallback(viewModel.networkCallback);
            Log.i(TAG, "Register network callback");
        } catch (e: Exception) {
            throw e;
        }
    }

    private fun unregisterNetworkCallback() {
        try {
            Log.i(TAG, "unregister");
            val connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(viewModel.networkCallback);
        } catch (e: Exception) {
            throw e;
        }
    }

    private fun unbindObserver() {
        Log.i(TAG, "Unbind observers");
        viewModel.toText.removeObservers(this);
        viewModel.fromText.removeObservers(this);
        viewModel.fromCurrency.removeObservers(this);
        viewModel.toCurrency.removeObservers(this);
        viewModel.error.removeObservers(this);
        viewModel.isLoading.removeObservers(this);
    }

    private fun bindObserver() {
        Log.i(TAG, "Bind observers");
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
        viewModel.error.observe(this) { error ->
            if (error == null) {
                binding.error.text = resources.getString(R.string.connected);
                binding.error.backgroundTintList = resources.getColorStateList(R.color.noLongerErrorBackground, null);
                handler.postDelayed({
                    if (viewModel.error.value == null) binding.error.visibility = INVISIBLE;
                }, 1000);
            } else {
                binding.error.text = when (error) {
                    HomeViewModel.Error.NO_INTERNET -> resources.getString(R.string.no_internet_error)
                    HomeViewModel.Error.FETCH_DATA -> resources.getString(R.string.no_internet_error)
                    else -> throw Exception("Unhandled error")
                }
                binding.error.backgroundTintList = resources.getColorStateList(R.color.errorBackground, null);
                binding.error.visibility = VISIBLE;
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.refresh.setBackgroundResource(R.drawable.loading);
            } else {
                binding.refresh.setBackgroundResource(R.drawable.refresh);
            }
        }
    }
}