package com.example.currencyconverter.kotlin

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.ShareActionProvider
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.animation.core.animateDecay
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuItemCompat
import androidx.core.view.children
import com.example.currencyconverter.R
import com.example.currencyconverter.kotlin.adapters.AdapterUtils
import com.example.currencyconverter.kotlin.adapters.CurrencyListAdapter
import com.example.currencyconverter.kotlin.singleton.ExchangeRateDatabaseSingleton
import okhttp3.OkHttpClient
import org.w3c.dom.Text
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var shareActionProvider : ShareActionProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy : StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val spinner1:Spinner = findViewById(R.id.spinner1)
        val spinner2:Spinner = findViewById(R.id.spinner2)

        val amountView : EditText = findViewById(R.id.moneyAmountText)

        val calcButton : Button = findViewById(R.id.calcButton)

        val resultView : TextView = findViewById(R.id.resultView)

        val currencies: Array<String> = ExchangeRateDatabaseSingleton.currencies

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
                val result = ExchangeRateDatabaseSingleton.convert(moneyAmount, currencyFrom.uppercase(), currencyTo.uppercase())
                resultView.text = String.format(Locale.GERMANY,"%.2f", result)
                setShareText("$moneyAmount ${currencyFrom.uppercase()} equals ${resultView.text} ${currencyTo.uppercase()}")
            }
            else{
                Toast.makeText(applicationContext, "Amount of money cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        menu?.findItem(R.id.currListItem)?.title = applicationContext.getString(R.string.currency_list)

        val shareItem : MenuItem = (menu?.findItem(R.id.action_share))!!
        shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
        setShareText(null)

        menu.findItem(R.id.edit_mode)?.isVisible = false
        menu.findItem(R.id.confirm_button)?.isVisible = false
        menu.findItem(R.id.currListItem)?.isVisible = true
        menu.findItem(R.id.action_share)?.isVisible = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.currListItem -> {
                val intent = Intent(this, CurrencyListActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.refreshRatesItem -> {
                ExchangeRateDatabaseSingleton.updateRates()
                AdapterUtils.notifyAdaptersInView(findViewById(android.R.id.content))

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setShareText(text : String?){
        val shareIntent : Intent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        if(text!=null){
            shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        }
        shareActionProvider.setShareIntent(shareIntent)
    }



}
