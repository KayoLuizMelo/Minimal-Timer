package com.kayo.minimaltimer.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

/**
 * MÓDULO 7: Serviço HTTP para consumir dados JSON (OkHttp + Gson).
 */
object ProductivityService {

    private val client = OkHttpClient()
    private val gson = Gson()
    private const val API_URL = "https://zenquotes.io/api/random"

    interface QuoteCallback {
        fun onSuccess(quote: String, author: String)
        fun onFailure(error: String)
    }

    data class ZenQuote(val q: String, val a: String)

    fun fetchRandomQuote(callback: QuoteCallback) {
        val request = Request.Builder()
            .url(API_URL)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure("Erro de rede: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    try {
                        val listType = object : TypeToken<List<ZenQuote>>() {}.type
                        val quotes: List<ZenQuote> = gson.fromJson(body, listType)
                        if (quotes.isNotEmpty()) {
                            callback.onSuccess(quotes[0].q, quotes[0].a)
                        } else {
                            callback.onFailure("JSON vazio")
                        }
                    } catch (e: Exception) {
                        callback.onFailure("Erro ao processar JSON")
                    }
                } else {
                    callback.onFailure("Erro no servidor: ${response.code}")
                }
            }
        })
    }
}
