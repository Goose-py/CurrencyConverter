package com.example.currencyconverter.kotlin.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import java.util.Locale


class ExchangeRateDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION: Int = 1
        const val DATABASE_NAME: String = "CurrencyExchange.db"
        const val ER_TABLE: String = "Rate2Euro"
        const val ER_COL_CODE: String = "code"
        const val ER_COL_RATE: String = "exRate"
        const val SQL_CREATE_ENTRIES: String =
            "CREATE TABLE $ER_TABLE (${BaseColumns._ID} INTEGER PRIMARY KEY, $ER_COL_CODE TEXT UNIQUE, $ER_COL_RATE REAL)"
        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $ER_TABLE"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun updateExchangeRate(countryCode: String, rate: Double): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ER_COL_CODE, countryCode)
            put(ER_COL_RATE, String.format(Locale.GERMANY, "%.2f", rate))
        }

        // Attempt to update the existing row
        val rowsUpdated = db.update(ER_TABLE, values, "$ER_COL_CODE = ?", arrayOf(countryCode))

        // If no rows were updated, insert a new row
        if (rowsUpdated == 0) {
            val newRowId = db.insert(ER_TABLE, null, values)
            return newRowId != -1L
        }

        return true
    }
}