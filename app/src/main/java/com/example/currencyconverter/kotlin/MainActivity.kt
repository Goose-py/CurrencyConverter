package com.example.currencyconverter.kotlin

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.ShareActionProvider
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.currencyconverter.R
import com.example.currencyconverter.kotlin.adapters.AdapterUtils
import com.example.currencyconverter.kotlin.adapters.CurrencyListAdapter
import com.example.currencyconverter.kotlin.db.ExchangeRateDbHelper
import com.example.currencyconverter.kotlin.singleton.runnables.CurrencyUpdateRunnableSingleton
import com.example.currencyconverter.kotlin.singleton.ExchangeRateDatabaseSingleton
import com.example.currencyconverter.kotlin.singleton.UpdateRatesThreadManagerSingleton
import com.example.currencyconverter.kotlin.worker.DailyUpdateWorker
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var shareActionProvider : ShareActionProvider
    private val updateRatesRunnable = CurrencyUpdateRunnableSingleton
    private lateinit var updateRatesThread : Thread
    private lateinit var dbHelper : ExchangeRateDbHelper
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

        updateRatesThread = Thread(updateRatesRunnable)
        updateRatesThread.start()
        UpdateRatesThreadManagerSingleton.setThread(updateRatesThread)

        dbHelper = ExchangeRateDbHelper(this)


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
                currencyTo= "EUR"
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

        scheduleDailyUpdate()

    }

    private fun scheduleDailyUpdate() {
        val dailyUpdateRequest = PeriodicWorkRequest.Builder(DailyUpdateWorker::class.java, 15, TimeUnit.MINUTES).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "DailyUpdater",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyUpdateRequest
        )

    }

    override fun onPause() {
        super.onPause()

        val prefs = getPreferences(MODE_PRIVATE)
        val editor = prefs.edit()
        val spinner1 = findViewById<Spinner>(R.id.spinner1)
        val spinner2 = findViewById<Spinner>(R.id.spinner2)
        val editText = findViewById<EditText>(R.id.moneyAmountText)

        editor.putInt("currencyFrom", spinner1.firstVisiblePosition)
        editor.putInt("currencyTo", spinner2.firstVisiblePosition)
        editor.putString("amount", editText.text.toString())
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        val prefs = getPreferences(MODE_PRIVATE)

        val spinner1 = findViewById<Spinner>(R.id.spinner1)
        val spinner2 = findViewById<Spinner>(R.id.spinner2)
        val editText = findViewById<EditText>(R.id.moneyAmountText)

        val currencyFrom = prefs.getInt("currencyFrom", 0)
        val currencyTo = prefs.getInt("currencyTo", 0)
        val amount = prefs.getString("amount", null)

        spinner1.setSelection(currencyFrom)
        spinner2.setSelection(currencyTo)
        editText.setText(amount)


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
                Toast.makeText(this, "Updating rates started!", Toast.LENGTH_SHORT).show()
                updateRatesRunnable.start()
                for(i in 0 until ExchangeRateDatabaseSingleton.currencies.size){
                    val currentCurrency = ExchangeRateDatabaseSingleton.currencies[i]
                    val er = ExchangeRateDatabaseSingleton.getExchangeRate(currentCurrency)
                    val updated = dbHelper.updateExchangeRate(currentCurrency, er)
                    if(updated){
                        Log.i("DATABASE UPDATE", "Updated exchange rate for $currentCurrency to $er")
                    }
                }
                AdapterUtils.notifyAdaptersInView(findViewById(android.R.id.content))
                Toast.makeText(this, "Updating rates finished!", Toast.LENGTH_SHORT).show()
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
