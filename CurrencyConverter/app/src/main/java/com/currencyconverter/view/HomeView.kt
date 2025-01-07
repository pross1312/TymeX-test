package com.currencyconverter.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.currencyconverter.R
import com.currencyconverter.databinding.ActivityHomeBinding
import com.currencyconverter.viewmodel.CurrencyInputState
import com.currencyconverter.viewmodel.HomeViewModel

class HomeView : AppCompatActivity(R.layout.activity_home) {
    lateinit var binding: ActivityHomeBinding;

    val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    val allCurrencies: Array<CharSequence> by lazy {
        viewModel.currencyList()
    }

    val fromCurrencyInput: CurrencyInputFragment by lazy {
        CurrencyInputFragment(allCurrencies, "From", object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                toCurrencyInput.viewModel.state.value = CurrencyInputState(
                    viewModel.convertForward(s.toString()),
                    toCurrencyInput.viewModel.state.value?.currency ?: 0
                );
            }
        })
    }

    val toCurrencyInput: CurrencyInputFragment by lazy {
        CurrencyInputFragment(allCurrencies, "To", object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                fromCurrencyInput.viewModel.state.value = CurrencyInputState(
                    viewModel.convertBackward(s.toString()),
                    fromCurrencyInput.viewModel.state.value?.currency ?: 0
                );
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            binding = ActivityHomeBinding.inflate(layoutInflater);
            supportFragmentManager.commit {
                add(binding.fromCurrencyInput.id, fromCurrencyInput);
                add(binding.toCurrencyInput.id, toCurrencyInput);
                setReorderingAllowed(true);
            };
            setContentView(binding.root);
        }
    }
}