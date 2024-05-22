package com.example.currencyconverter.kotlin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.ComponentActivity
import com.example.currencyconverter.R
import com.example.currencyconverter.java.exchange.ExchangeRateDatabase
import com.example.currencyconverter.kotlin.adapters.CurrencyListAdapter

class CurrencyListActivity : ComponentActivity() {
    private val exchangeRateDatabaseObj = ExchangeRateDatabase()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_list)

        val data = exchangeRateDatabaseObj.currencies
        val listView = findViewById<ListView>(R.id.currency_list_view)

        val adapter = CurrencyListAdapter(this, data.toList())
        listView.adapter = adapter


    }
}