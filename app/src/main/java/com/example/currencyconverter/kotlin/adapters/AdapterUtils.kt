package com.example.currencyconverter.kotlin.adapters

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter

object AdapterUtils {
    fun notifyAdaptersInView(view: View) {
        if (view is AdapterView<*>) {
            val adapter = view.adapter
            if (adapter is BaseAdapter) {
                adapter.notifyDataSetChanged()
                Log.i("AdapterUtils", "Adapter notified: $adapter")
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                notifyAdaptersInView(child)
            }
        }
    }
}