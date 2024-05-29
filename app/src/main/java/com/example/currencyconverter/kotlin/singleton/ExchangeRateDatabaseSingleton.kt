package com.example.currencyconverter.kotlin.singleton

import android.util.Log
import com.example.currencyconverter.java.exchange.ExchangeRateDatabase
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object ExchangeRateDatabaseSingleton : ExchangeRateDatabase() {

    private val client = OkHttpClient()
    init{
        updateRates()
    }

    fun updateRates(){
        try {
            val request: Request =
                Request.Builder().url("https://www.floatrates.com/daily/eur.json").build()
            val response: Response = client.newCall(request).execute()
            val responseBody: String? = response.body?.string()
            val root = JSONObject(responseBody!!)
            val currencies = this.currencies
            for (i in currencies.indices) {
                if(currencies[i] == "EUR" || currencies[i] == "HRK") continue
                val currency = root.getJSONObject(currencies[i].lowercase())
                this.setExchangeRate(
                    currency.getString("code").uppercase(),
                    currency.getString("rate").toDouble()
                )

            }
        }catch (exception : IOException){
            Log.e("Refresh Rates", "Can't query the database")
            exception.printStackTrace()
        }catch (exception: JSONException){
            Log.e("Refresh Rates", "Error parsing JSON response")
            exception.printStackTrace()
        }
    }
}