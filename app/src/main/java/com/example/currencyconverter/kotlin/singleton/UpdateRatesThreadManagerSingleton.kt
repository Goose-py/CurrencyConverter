package com.example.currencyconverter.kotlin.singleton

object UpdateRatesThreadManagerSingleton : Thread(){
    private var thread : Thread? = null

    fun getThread() : Thread?{
        return thread
    }

    fun setThread(t : Thread?){
        thread = t
    }
}