package com.example.currencyconverter.kotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.currencyconverter.R
import com.example.currencyconverter.kotlin.adapters.AdapterUtils
import com.example.currencyconverter.kotlin.adapters.CurrencyListAdapter
import com.example.currencyconverter.kotlin.singleton.runnables.CurrencyUpdateRunnableSingleton
import com.example.currencyconverter.kotlin.singleton.ExchangeRateDatabaseSingleton

class CurrencyListActivity : AppCompatActivity() {
    private var editMode = false
    private lateinit var toolbar : Toolbar
    private lateinit var adapter : CurrencyListAdapter
    private val updateRatesRunnable = CurrencyUpdateRunnableSingleton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_list)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val data = ExchangeRateDatabaseSingleton.currencies
        val listView = findViewById<ListView>(R.id.currency_list_view)

        adapter = CurrencyListAdapter(this, data.toList())
        listView.adapter = adapter

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedEntry = adapter.getItem(position)
                if(!editMode){
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${ExchangeRateDatabaseSingleton.getCapital(selectedEntry.toString())}"))
                    if(intent.resolveActivity(packageManager)!=null){
                        startActivity(intent)
                    }else{
                        Toast.makeText(applicationContext, "No application can handle this request.", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    val intent = Intent(this, EditCurrencyActivity::class.java)
                    intent.putExtra("currencyName", selectedEntry.toString())
                    startActivity(intent)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.findItem(R.id.currListItem)?.title = applicationContext.getString(R.string.app_name)
        menu?.findItem(R.id.edit_mode)?.isVisible = true
        menu?.findItem(R.id.confirm_button)?.isVisible = false
        menu?.findItem(R.id.currListItem)?.isVisible = true
        menu?.findItem(R.id.action_share)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.currListItem -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.edit_mode -> {
                editMode = !editMode
                if(editMode){
                toolbar.setBackgroundColor(resources.getColor(R.color.editModeColor))}
                else{
                    toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                }
                true
            }
            R.id.refreshRatesItem -> {
                Toast.makeText(this, "Updating rates started!", Toast.LENGTH_SHORT).show()
                updateRatesRunnable.start()
                AdapterUtils.notifyAdaptersInView(findViewById(android.R.id.content))
                Toast.makeText(this, "Updating rates finished!", Toast.LENGTH_SHORT).show()
                true
            }
            else->onOptionsItemSelected(item)
        }
    }
}