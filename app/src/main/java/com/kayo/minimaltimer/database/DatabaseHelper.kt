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
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_HISTORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DURATION + " INTEGER,"
                + COLUMN_DATE + " TEXT" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        onCreate(db)
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
