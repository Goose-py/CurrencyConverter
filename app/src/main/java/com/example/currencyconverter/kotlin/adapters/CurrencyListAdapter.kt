package com.example.currencyconverter.kotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.currencyconverter.R
import android.widget.TextView
import com.example.currencyconverter.kotlin.singleton.ExchangeRateDatabaseSingleton
import java.util.Locale


class CurrencyListAdapter(private var context : Context, private var data : List<String>) : BaseAdapter() {


    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val entry = data[position]
        val view = convertView?:LayoutInflater.from(context).inflate(R.layout.list_view_item, null, false)

        val currency:TextView? = view?.findViewById(R.id.currencyName)
        currency?.text = entry

        val exchangeRate:TextView? = view?.findViewById(R.id.exchangeRate)
        exchangeRate?.text = String.format(Locale.GERMANY, "%.2f", ExchangeRateDatabaseSingleton.getExchangeRate(entry))

        val flag: ImageView? = view?.findViewById(R.id.flag)
        val imgResource = context.resources.getIdentifier("@drawable/flag_${currency?.text.toString().lowercase()}", "drawable", context.packageName)
        flag?.setImageResource(imgResource)


        return view
    }



}