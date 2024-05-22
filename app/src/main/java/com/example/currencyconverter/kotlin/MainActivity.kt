package com.example.currencyconverter.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.currencyconverter.R
import com.example.currencyconverter.java.exchange.ExchangeRateDatabase
import com.example.currencyconverter.kotlin.adapters.CurrencyListAdapter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val spinner1:Spinner = findViewById(R.id.spinner)
        val spinner2:Spinner = findViewById(R.id.spinner2)

        val amountView : EditText = findViewById(R.id.moneyAmountText)

        val calcButton : Button = findViewById(R.id.calcButton)

        val resultView : TextView = findViewById(R.id.resultView)

        val switchToCurrencyListButton : Button = findViewById(R.id.switchToCurrList)

        val exchangeRateDatabaseObj =
            ExchangeRateDatabase()
        val currencies: Array<String> = exchangeRateDatabaseObj.currencies

        val adapter = CurrencyListAdapter(this, currencies.toList())

        spinner1.adapter = adapter
        spinner2.adapter = adapter


        var currencyFrom = "EUR"
        var currencyTo = "EUR"


        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (parent != null) {
                    currencyFrom = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                currencyFrom = "EUR"
            }
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (parent != null) {
                    currencyTo = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                currencyTo = "EUR"
            }
        }

        calcButton.setOnClickListener{
            val moneyAmount : Double? = amountView.text.toString().toDoubleOrNull()
            if(moneyAmount != null){
                val result = exchangeRateDatabaseObj.convert(moneyAmount, currencyFrom.uppercase(), currencyTo.uppercase())
                resultView.text = String.format(Locale.GERMANY,"%.2f", result)
            }
            else{
                Toast.makeText(applicationContext, "Amount of money cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        switchToCurrencyListButton.setOnClickListener{
            val intent = Intent(this, CurrencyListActivity::class.java)
            startActivity(intent)
        }

    }



}