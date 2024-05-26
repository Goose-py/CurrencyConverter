package com.example.currencyconverter.kotlin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.currencyconverter.R
import com.example.currencyconverter.kotlin.singleton.ExchangeRateDatabaseSingleton

class EditCurrencyActivity : AppCompatActivity() {

    private lateinit var currentCurrency : String
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_currency_activity)
        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val oldExchangeRateView : TextView = findViewById(R.id.oldExchangeRateText)
        val currentCurrencyView : TextView = findViewById(R.id.currency)
        val flagImgView : ImageView = findViewById(R.id.flagLogo)

        currentCurrency = intent.getStringExtra("currencyName")!!
        val flag = applicationContext.resources.getIdentifier("@drawable/flag_${currentCurrency?.lowercase()}", "drawable", applicationContext.packageName)

        flagImgView.setImageResource(flag)
        currentCurrencyView.text = currentCurrency
        oldExchangeRateView.text = "${oldExchangeRateView.text}  ${ExchangeRateDatabaseSingleton.getExchangeRate(currentCurrency?.uppercase())}"

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.findItem(R.id.currListItem)?.isVisible = false
        menu?.findItem(R.id.edit_mode)?.isVisible = false
        menu?.findItem(R.id.action_share)?.isVisible = false
        menu?.findItem(R.id.confirm_button)?.isVisible = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.confirm_button -> {
                val newExchangeRateView : EditText = findViewById(R.id.newExchangeRate)
                val newExchangeRate = newExchangeRateView.text.toString()
                if(newExchangeRate != ""){
                    ExchangeRateDatabaseSingleton.setExchangeRate(currentCurrency.uppercase(), newExchangeRateView.text.toString().toDouble())
                }else{
                    Toast.makeText(this, "Exchange rate not changed", Toast.LENGTH_SHORT)
                }
                val intent = Intent(this, CurrencyListActivity::class.java)
                startActivity(intent)
                true
            }
            else->onOptionsItemSelected(item)
        }
    }
}
