package com.example.currencyconverter.kotlin.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.currencyconverter.kotlin.db.ExchangeRateDbHelper
import com.example.currencyconverter.kotlin.singleton.ExchangeRateDatabaseSingleton
import com.example.currencyconverter.kotlin.singleton.runnables.CurrencyUpdateRunnableSingleton


class DailyUpdateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val dbHelper = ExchangeRateDbHelper(context)
    override fun doWork(): Result {
        try {
            CurrencyUpdateRunnableSingleton.start()
            for(i in 0 until ExchangeRateDatabaseSingleton.currencies.size){
                val currentCurrency = ExchangeRateDatabaseSingleton.currencies[i]
                val er = ExchangeRateDatabaseSingleton.getExchangeRate(currentCurrency)
                val updated = dbHelper.updateExchangeRate(currentCurrency, er)
                if(updated){
                    Log.i("DATABASE UPDATE", "Updated exchange rate for $currentCurrency to $er")
                }
            }

        } catch (e: InterruptedException) {
        }
        Log.i("DAILY UPDATE", "DAILY UPDATE DONE")
        return Result.success()
    }
}