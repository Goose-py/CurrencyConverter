package com.example.currencyconverter.kotlin.singleton.runnables

import android.util.Log
import com.example.currencyconverter.kotlin.singleton.ExchangeRateDatabaseSingleton

object CurrencyUpdateRunnableSingleton : Runnable {
    private var running = false

    override fun run() {
        while(true){
            synchronized(this) {
                if(running) {
                    Log.i("UPDATE RATES", "Update started!")
                    ExchangeRateDatabaseSingleton.updateRates()
                    Log.i("UPDATE RATES", "Update finished!")
                    running = false
                }
            }
            try {
                Thread.sleep(1000)
            } catch (_: InterruptedException) {
            }
        }
    }

    fun start(){
        running = true
    }
}