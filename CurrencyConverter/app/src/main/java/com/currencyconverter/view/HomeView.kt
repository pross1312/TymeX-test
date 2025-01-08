package com.currencyconverter.view

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.currencyconverter.R
import com.currencyconverter.data.api.OpenExchangeApi
import com.currencyconverter.databinding.ActivityHomeBinding
import com.currencyconverter.databinding.SpinnerTextBinding
import com.currencyconverter.view.adapter.CurrencySpinnerAdapter
import com.currencyconverter.viewmodel.HomeViewModel


class HomeView: AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding;
    private val api: OpenExchangeApi = OpenExchangeApi("902e8d27de3f412f96039e606b769039");
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory(api) };

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater);
        val adapter = CurrencySpinnerAdapter(this, viewModel.allCurrencies);
        binding.viewModel = viewModel;
        binding.spinnerFrom.adapter = adapter;
        binding.spinnerTo.adapter = adapter;
        binding.lifecycleOwner = this;
        setContentView(binding.root);
        if (!haveNetworkConnection()) {
            Log.i("HomePage", "No network");
        }
    }

    private fun haveNetworkConnection(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals(
                    "WIFI",
                    ignoreCase = true
                )
            ) if (ni.isConnected) haveConnectedWifi = true
            if (ni.typeName.equals(
                    "MOBILE",
                    ignoreCase = true
                )
            ) if (ni.isConnected) haveConnectedMobile = true
        }
        return haveConnectedWifi || haveConnectedMobile
    }
}