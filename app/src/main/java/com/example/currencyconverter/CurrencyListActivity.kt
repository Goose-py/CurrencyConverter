package com.example.currencyconverter

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.ComponentActivity

class CurrencyListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_list)

        val listView = findViewById<ListView>(R.id.listView)

        val exchangeRateDatabaseObj = ExchangeRateDatabase()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            exchangeRateDatabaseObj.currencies
        )
        listView.adapter = adapter

    }
}