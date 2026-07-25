package com.kayo.minimaltimer.utils

import android.content.Context
import android.widget.Toast
import java.util.Locale

/**
 * MÓDULO 4: HelperMethods para centralizar funções reutilizáveis.
 */
object HelperMethods {

    /**
     * Exibe um Toast simplificado.
     */
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Formata o tempo em MM:SS.
     */
    fun formatTime(minutes: Int, seconds: Int = 0): String {
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}
