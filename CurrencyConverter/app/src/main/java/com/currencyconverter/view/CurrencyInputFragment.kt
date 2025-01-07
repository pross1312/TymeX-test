package com.currencyconverter.view

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.currencyconverter.R;
import com.currencyconverter.databinding.CurrencyInputBinding;
import com.currencyconverter.viewmodel.CurrencyInputViewModel

class CurrencyInputFragment(
    private val currencies: Array<CharSequence>,
    private val label: String,
    private val textWatcher: TextWatcher
): Fragment() {
    val viewModel: CurrencyInputViewModel by lazy {
        ViewModelProvider(this)[CurrencyInputViewModel::class.java]
    }

    var _binding: CurrencyInputBinding? = null;
    val binding get() = _binding!!;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CurrencyInputBinding.inflate(inflater, container, false);
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_text, this.currencies);
        binding.spinner.adapter = adapter;
        viewModel.label = this.label;
        binding.viewModel = this.viewModel;
        binding.lifecycleOwner = this;
        binding.text.addTextChangedListener(textWatcher);
        return binding.root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null;
    }
}