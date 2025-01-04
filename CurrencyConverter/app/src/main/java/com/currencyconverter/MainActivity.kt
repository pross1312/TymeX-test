package com.currencyconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.currencyconverter.databinding.ActivityMainBinding
import com.currencyconverter.databinding.ItemViewBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;
    private var clicked = false;

    private val tests: List<Int> = List(10) { it }

    class TestViewHolder(private val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Int) {
            binding.text.text = data.toString();
        }
    }

    class TestAdapter(private val tests: List<Int>) : RecyclerView.Adapter<TestViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
            val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false);
            return TestViewHolder(binding);
        }

        override fun getItemCount(): Int {
            return tests.size
        }

        override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
            holder.bind(tests[position]);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);
    }

    override fun onStart() {
        super.onStart()

        binding.button.setOnClickListener {
            clicked = !clicked;
            if (clicked) binding.text.text = "You have clicked me!!!";
            else binding.text.text = "Hello world";
        }
        binding.list.adapter = TestAdapter(tests);
        val adapter = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, tests);
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        binding.spinner.adapter = adapter;
        binding.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.text.text = tests[position].toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.text.text = ""
            }
        }
    }
}