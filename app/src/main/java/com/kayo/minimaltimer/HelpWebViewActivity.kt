package com.kayo.minimaltimer

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class HelpWebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_web_view)

        // MÓDULO 4: Uso de WebView para exibir conteúdo web
        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        
        // Carrega uma página de exemplo (ex.: Documentação do Android)
        webView.loadUrl("https://developer.android.com/guide")
    }
}
