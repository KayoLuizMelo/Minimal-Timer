package com.kayo.minimaltimer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Nome que vai aparecer no Logcat para rastrearmos o Ciclo de Vida
    private val TAG_CICLO = "CicloDeVidaApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG_CICLO, "onCreate: A tela está sendo criada na memória.")

        // 1. Colocando o Fragment do Timer dentro do espaço vazio da tela
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TimerFragment())
                .commit()
        }

        // 2. Configurando o botão para abrir a tela de Configurações (Navegação + Dados)
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            // Cria a intenção de ir para a SettingsActivity
            val intencao = Intent(this, SettingsActivity::class.java)

            // Coloca um dado extra dentro da intenção (Chave: "TEMPO_PADRAO", Valor: 25)
            intencao.putExtra("TEMPO_PADRAO", 25)

            // Inicia a nova tela
            startActivity(intencao)
        }
    }

    // --- Monitoramento do Ciclo de Vida exigido pela faculdade ---
    override fun onStart() {
        super.onStart()
        Log.d(TAG_CICLO, "onStart: A tela ficou visível para o usuário.")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG_CICLO, "onResume: O aplicativo está pronto para interação.")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG_CICLO, "onPause: O app perdeu o foco (ex: abrindo outra tela).")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG_CICLO, "onStop: A tela não está mais visível.")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG_CICLO, "onDestroy: O aplicativo foi fechado/destruído.")
    }
}