package com.example.currencyconverter.kotlin.adapters

import android.content.Context
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.currencyconverter.R
import android.widget.TextView
import com.example.currencyconverter.kotlin.db.ExchangeRateDbHelper
import java.util.Locale


class CurrencyListAdapter(private var context : Context, private var data : List<String>) : BaseAdapter() {

    val dbHelper = ExchangeRateDbHelper(context)
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

        val exchangeRate: TextView? = view?.findViewById(R.id.exchangeRate)
        val db = dbHelper.writableDatabase
        val projection: Array<String> = listOf(
            BaseColumns._ID,
            ExchangeRateDbHelper.ER_COL_NAME,
            ExchangeRateDbHelper.ER_COL_RATE
        ).toTypedArray()
        val selection = "${ExchangeRateDbHelper.ER_COL_NAME} = ?"
        val c = db.query(
            ExchangeRateDbHelper.ER_TABLE, projection, selection,
            listOf(entry.uppercase()).toTypedArray(), null, null, null
        )
        if (c.moveToFirst()) {
            val rate = c.getString(c.getColumnIndexOrThrow(ExchangeRateDbHelper.ER_COL_RATE))
            exchangeRate?.text = rate.toString()
            c.close()
        }

        val flag: ImageView? = view?.findViewById(R.id.flag)
        val imgResource = context.resources.getIdentifier("@drawable/flag_${currency?.text.toString().lowercase()}", "drawable", context.packageName)
        flag?.setImageResource(imgResource)


        return view
    }



}