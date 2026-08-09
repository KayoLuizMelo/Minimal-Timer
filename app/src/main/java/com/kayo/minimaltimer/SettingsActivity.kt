package com.kayo.minimaltimer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.kayo.minimaltimer.database.DatabaseHelper
import com.kayo.minimaltimer.utils.HelperMethods

class SettingsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private val PREFS_NAME = "MinimalTimerPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        dbHelper = DatabaseHelper(this)

        val tvDadosRecebidos = findViewById<TextView>(R.id.tvDadosRecebidos)
        val etDefaultTime = findViewById<EditText>(R.id.etDefaultTime)
        val btnSavePrefs = findViewById<Button>(R.id.btnSavePrefs)
        val btnViewHistory = findViewById<Button>(R.id.btnViewHistory)
        val tvHistoryDisplay = findViewById<TextView>(R.id.tvHistoryDisplay)

        // 1. Recebendo dado via Intent
        val tempoEnviado = intent.getIntExtra("TEMPO_PADRAO", 25)
        tvDadosRecebidos.text = "Timer atual: $tempoEnviado min (via Intent)."

        // 2. SharedPreferences: Recuperar tempo padrão
        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedTime = sharedPrefs.getInt("default_time", 25)
        etDefaultTime.setText(savedTime.toString())

        btnSavePrefs.setOnClickListener {
            val time = etDefaultTime.text.toString().toIntOrNull() ?: 25
            sharedPrefs.edit {
                putInt("default_time", time)
            }
            HelperMethods.showToast(this, "Tempo padrão salvo!")
        }

        // 3. SQLite: Exibir histórico simplificado
        btnViewHistory.setOnClickListener {
            val history = dbHelper.getAllHistory()
            if (history.isEmpty()) {
                tvHistoryDisplay.text = "Nenhuma sessão finalizada encontrada."
            } else {
                tvHistoryDisplay.text = history.joinToString("\n")
            }
        }
        
        // Carrega o histórico ao abrir
        btnViewHistory.performClick()
    }
}
