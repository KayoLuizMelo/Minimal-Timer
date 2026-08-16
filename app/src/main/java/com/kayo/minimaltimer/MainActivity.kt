package com.kayo.minimaltimer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kayo.minimaltimer.utils.ProductivityService

class MainActivity : AppCompatActivity() {

    // Nome que vai aparecer no Logcat para rastrearmos o Ciclo de Vida
    private val TAG_CICLO = "CicloDeVidaApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG_CICLO, "onCreate: A tela está sendo criada na memória.")

        val tvQuote = findViewById<TextView>(R.id.tvQuote)

        // MÓDULO 7: Consumindo API externa (JSON/HTTP)
        ProductivityService.fetchRandomQuote(object : ProductivityService.QuoteCallback {
            override fun onSuccess(quote: String, author: String) {
                runOnUiThread {
                    tvQuote.text = "\"$quote\" - $author"
                }
            }

            override fun onFailure(error: String) {
                runOnUiThread {
                    tvQuote.text = "Foco e Produtividade!"
                }
            }
        })

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

    // MÓDULO 4: Criação do Menu de Opções (Ações Globais)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_gallery -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, GalleryFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }
            R.id.menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_help -> {
                val intent = Intent(this, HelpWebViewActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_contact -> {
                val intent = Intent(this, ContactActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}