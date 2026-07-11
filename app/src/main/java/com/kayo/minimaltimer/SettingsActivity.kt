package com.kayo.minimaltimer

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Encontra o texto no XML
        val tvDadosRecebidos = findViewById<TextView>(R.id.tvDadosRecebidos)

        // Pega o dado enviado pela primeira tela (se não achar nada, assume 25)
        val tempoEnviado = intent.getIntExtra("TEMPO_PADRAO", 25)

        // Altera o texto na tela exibindo o dado recebido
        tvDadosRecebidos.text = "O timer atual está programado para: $tempoEnviado minutos."
    }
}