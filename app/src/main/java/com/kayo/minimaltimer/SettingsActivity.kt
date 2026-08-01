package com.kayo.minimaltimer

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.kayo.minimaltimer.database.DatabaseHelper
import com.kayo.minimaltimer.utils.HelperMethods
import java.io.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private val PREFS_NAME = "MinimalTimerPrefs"
    private val FILE_NAME_INTERNAL = "session_log.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        dbHelper = DatabaseHelper(this)

        val tvDadosRecebidos = findViewById<TextView>(R.id.tvDadosRecebidos)
        val etDefaultTime = findViewById<EditText>(R.id.etDefaultTime)
        val btnSavePrefs = findViewById<Button>(R.id.btnSavePrefs)
        val btnSaveInternal = findViewById<Button>(R.id.btnSaveInternal)
        val btnReadInternal = findViewById<Button>(R.id.btnReadInternal)
        val btnSaveExternal = findViewById<Button>(R.id.btnSaveExternal)
        val btnReadRaw = findViewById<Button>(R.id.btnReadRaw)
        val btnViewHistory = findViewById<Button>(R.id.btnViewHistory)
        val tvHistoryDisplay = findViewById<TextView>(R.id.tvHistoryDisplay)

        // 1. Recebendo dado via Intent (Módulo 2/3)
        val tempoEnviado = intent.getIntExtra("TEMPO_PADRAO", 25)
        tvDadosRecebidos.text = "O timer atual veio da Intent com: $tempoEnviado minutos."

        // 2. SharedPreferences: Recuperar dados
        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedTime = sharedPrefs.getInt("default_time", 25)
        etDefaultTime.setText(savedTime.toString())

        btnSavePrefs.setOnClickListener {
            val time = etDefaultTime.text.toString().toIntOrNull() ?: 25
            sharedPrefs.edit {
                putInt("default_time", time)
            }
            HelperMethods.showToast(this, "Preferência salva!")
        }

        // 3. Armazenamento Interno: FileOutputStream
        btnSaveInternal.setOnClickListener {
            val data = "Log de Uso: App aberto em ${System.currentTimeMillis()}\n"
            try {
                val fos: FileOutputStream = openFileOutput(FILE_NAME_INTERNAL, Context.MODE_APPEND)
                fos.write(data.toByteArray())
                fos.close()
                HelperMethods.showToast(this, "Log gravado internamente!")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 4. Armazenamento Interno: FileInputStream e InputStreamReader
        btnReadInternal.setOnClickListener {
            try {
                val fis: FileInputStream = openFileInput(FILE_NAME_INTERNAL)
                val isr = InputStreamReader(fis)
                val br = BufferedReader(isr)
                val sb = StringBuilder()
                var text: String?
                while (br.readLine().also { text = it } != null) {
                    sb.append(text).append("\n")
                }
                tvHistoryDisplay.text = sb.toString()
            } catch (e: Exception) {
                HelperMethods.showToast(this, "Nenhum log encontrado.")
            }
        }

        // 5. Armazenamento Externo: getExternalFilesDir (Moderno/Seguro)
        btnSaveExternal.setOnClickListener {
            val state = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED == state) {
                val externalFile = File(getExternalFilesDir(null), "backup_minimal.txt")
                try {
                    val fos = FileOutputStream(externalFile)
                    fos.write("Backup de segurança do Minimal Timer".toByteArray())
                    fos.close()
                    HelperMethods.showToast(this, "Backup salvo em: ${externalFile.absolutePath}")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        // 6. Res/Raw: openRawResource
        btnReadRaw.setOnClickListener {
            try {
                val inputStream = resources.openRawResource(R.raw.app_info)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val sb = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line).append("\n")
                }
                tvHistoryDisplay.text = sb.toString()
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 7. SQLite: DatabaseHelper
        btnViewHistory.setOnClickListener {
            val history = dbHelper.getAllHistory()
            if (history.isEmpty()) {
                tvHistoryDisplay.text = "Nenhuma sessão registrada no banco."
            } else {
                tvHistoryDisplay.text = history.joinToString("\n")
            }
        }
    }
}
