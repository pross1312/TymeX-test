package com.currencyconverter.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.currencyconverter.databinding.SpinnerTextBinding

class CurrencySpinnerAdapter(lifecycleOwner: LifecycleOwner, private val data: MutableLiveData<Array<Map.Entry<String, String>>>): BaseAdapter() {

    init {
        data.observe(lifecycleOwner) { data ->
            super.notifyDataSetChanged()
        }
    }

    override fun getCount(): Int = data.value?.size ?: 0;

    override fun getItem(position: Int): Map.Entry<String, String>? = data.value?.get(position)
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = SpinnerTextBinding.inflate(LayoutInflater.from(parent?.context), parent, false);
        binding.text.text = getItem(position)?.value ?: ""
        return binding.root;
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = SpinnerTextBinding.inflate(LayoutInflater.from(parent?.context), parent, false);
        binding.text.text = getItem(position)?.key ?: ""
        return binding.root;
    }

    override fun getViewTypeCount(): Int = 1
}

