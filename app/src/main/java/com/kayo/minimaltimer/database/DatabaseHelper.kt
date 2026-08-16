package com.kayo.minimaltimer.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * MÓDULO 5: Classe auxiliar para o Banco de Dados SQLite.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "minimal_timer.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_HISTORY = "history"
        const val COLUMN_ID = "id"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_DATE = "date"

        // MÓDULO 7: Tabela para Timers Baseados em Localização
        const val TABLE_LOCATION_TIMERS = "location_timers"
        const val COLUMN_LAT = "lat"
        const val COLUMN_LNG = "lng"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_TIMER_MIN = "timer_min"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createHistoryTable = ("CREATE TABLE " + TABLE_HISTORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DURATION + " INTEGER,"
                + COLUMN_DATE + " TEXT" + ")")
        db?.execSQL(createHistoryTable)

        val createLocationTimersTable = ("CREATE TABLE " + TABLE_LOCATION_TIMERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LAT + " REAL,"
                + COLUMN_LNG + " REAL,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_TIMER_MIN + " INTEGER" + ")")
        db?.execSQL(createLocationTimersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LOCATION_TIMERS")
        onCreate(db)
    }

    /**
     * MÓDULO 7: Adiciona ou atualiza um timer para uma localização específica.
     */
    fun saveLocationTimer(lat: Double, lng: Double, address: String, minutes: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_LAT, lat)
        values.put(COLUMN_LNG, lng)
        values.put(COLUMN_ADDRESS, address)
        values.put(COLUMN_TIMER_MIN, minutes)
        
        // Tenta encontrar se já existe um timer próximo a esse local
        val result = db.insertWithOnConflict(TABLE_LOCATION_TIMERS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return result
    }

    /**
     * Busca o timer salvo para uma coordenada.
     */
    fun getLocationTimer(lat: Double, lng: Double): Int? {
        val db = this.readableDatabase
        // Procura em um raio pequeno (aproximadamente 100m)
        val cursor = db.rawQuery("SELECT $COLUMN_TIMER_MIN FROM $TABLE_LOCATION_TIMERS WHERE ABS($COLUMN_LAT - ?) < 0.001 AND ABS($COLUMN_LNG - ?) < 0.001", arrayOf(lat.toString(), lng.toString()))
        
        var minutes: Int? = null
        if (cursor.moveToFirst()) {
            minutes = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return minutes
    }

    /**
     * Adiciona um registro de sessão ao banco.
     */
    fun addSession(duration: Int, date: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_DURATION, duration)
        values.put(COLUMN_DATE, date)
        val success = db.insert(TABLE_HISTORY, null, values)
        db.close()
        return success
    }

    /**
     * Retorna todo o histórico.
     */
    fun getAllHistory(): List<String> {
        val historyList = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_HISTORY ORDER BY id DESC", null)
        
        if (cursor.moveToFirst()) {
            do {
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                historyList.add("$date - Sessão de $duration min")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return historyList
    }
}
