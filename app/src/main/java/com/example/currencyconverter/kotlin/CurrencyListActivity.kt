package com.example.currencyconverter.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.currencyconverter.R
import com.example.currencyconverter.java.exchange.ExchangeRateDatabase
import com.example.currencyconverter.kotlin.adapters.CurrencyListAdapter

class CurrencyListActivity : AppCompatActivity() {
    private val exchangeRateDatabaseObj = ExchangeRateDatabase()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_list)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val data = exchangeRateDatabaseObj.currencies
        val listView = findViewById<ListView>(R.id.currency_list_view)

        val adapter = CurrencyListAdapter(this, data.toList())
        listView.adapter = adapter


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.getItem(0)?.title = applicationContext.getString(R.string.app_name)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.currListItem ->{
                val intent : Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }else->onOptionsItemSelected(item)
        }
    }
}