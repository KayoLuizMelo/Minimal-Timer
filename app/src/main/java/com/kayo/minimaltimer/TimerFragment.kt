package com.kayo.minimaltimer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.kayo.minimaltimer.database.DatabaseHelper
import com.kayo.minimaltimer.utils.HelperMethods
import java.text.SimpleDateFormat
import java.util.*

class TimerFragment : Fragment() {

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 1500000 // 25 minutos padrão
    private var initialTimeSet: Long = 1500000
    private var isTimerRunning: Boolean = false
    private lateinit var dbHelper: DatabaseHelper
    private val TIMER_CHANNEL_ID = "TIMER_NOTIFICATION_CHANNEL"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        dbHelper = DatabaseHelper(requireContext())

        val tvTimer = view.findViewById<TextView>(R.id.tvTimer)
        val etCustomMinutes = view.findViewById<EditText>(R.id.etCustomMinutes)
        val etCustomSeconds = view.findViewById<EditText>(R.id.etCustomSeconds)
        val btnAplicarTempo = view.findViewById<Button>(R.id.btnAplicarTempo)
        val btnIniciar = view.findViewById<Button>(R.id.btnIniciar)
        val btnAjuda = view.findViewById<Button>(R.id.btnAjuda)
        val tvRecentHistory = view.findViewById<TextView>(R.id.tvRecentHistory)

        // Carrega preferências
        val sharedPrefs = requireActivity().getSharedPreferences("MinimalTimerPrefs", Context.MODE_PRIVATE)
        val defaultMinutes = sharedPrefs.getInt("default_time", 25)
        timeLeftInMillis = defaultMinutes * 60000L
        initialTimeSet = timeLeftInMillis
        updateCountDownText(tvTimer)
        updateRecentHistoryDisplay(tvRecentHistory)
        
        // MÓDULO 7: Verificação automática de Timer por Localização
        checkLocationTimer(tvTimer, btnIniciar)

        registerForContextMenu(tvTimer)

        btnAplicarTempo.setOnClickListener {
            val minText = etCustomMinutes.text.toString()
            val secText = etCustomSeconds.text.toString()

            val minutes = if (minText.isNotEmpty()) minText.toInt() else 0
            val seconds = if (secText.isNotEmpty()) secText.toInt() else 0

            if (minutes > 0 || seconds > 0) {
                if (minutes <= 180 && seconds < 60) {
                    pauseTimer(btnIniciar)
                    timeLeftInMillis = (minutes * 60000L) + (seconds * 1000L)
                    initialTimeSet = timeLeftInMillis
                    updateCountDownText(tvTimer)
                    
                    val timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                    saveToRecentHistory(timeString)
                    updateRecentHistoryDisplay(tvRecentHistory)
                    
                    HelperMethods.showToast(requireContext(), "Tempo definido: $timeString")
                } else {
                    HelperMethods.showToast(requireContext(), "Use minutos (0-180) e segundos (0-59)")
                }
            } else {
                HelperMethods.showToast(requireContext(), "Informe o tempo!")
            }
        }

        btnIniciar.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer(btnIniciar)
            } else {
                startTimer(tvTimer, btnIniciar)
            }
        }

        btnAjuda.setOnClickListener {
            val intent = Intent(requireContext(), HelpWebViewActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun saveToRecentHistory(timeString: String) {
        val prefs = requireActivity().getSharedPreferences("MinimalTimerPrefs", Context.MODE_PRIVATE)
        val history = prefs.getString("recent_history", "") ?: ""
        val historyList = if (history.isEmpty()) mutableListOf() else history.split(",").toMutableList()
        
        // Remove duplicata se já existir e adiciona no topo
        historyList.remove(timeString)
        historyList.add(0, timeString)
        
        // Mantém apenas os últimos 5
        val limitedList = if (historyList.size > 5) historyList.subList(0, 5) else historyList
        
        prefs.edit().putString("recent_history", limitedList.joinToString(",")).apply()
    }

    private fun updateRecentHistoryDisplay(textView: TextView) {
        val prefs = requireActivity().getSharedPreferences("MinimalTimerPrefs", Context.MODE_PRIVATE)
        val history = prefs.getString("recent_history", "") ?: ""
        if (history.isNotEmpty()) {
            textView.text = history.replace(",", "  |  ")
        } else {
            textView.text = "Nenhum tempo recente"
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.context_menu, menu)
        menu.setHeaderTitle("Opções do Timer")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val tvTimer = view?.findViewById<TextView>(R.id.tvTimer)
        val btnIniciar = view?.findViewById<Button>(R.id.btnIniciar)

        return when (item.itemId) {
            R.id.ctx_reset -> {
                pauseTimer(btnIniciar)
                timeLeftInMillis = 1500000 // 25 min default
                updateCountDownText(tvTimer)
                HelperMethods.showToast(requireContext(), "Timer reiniciado")
                true
            }
            R.id.ctx_copy -> {
                HelperMethods.showToast(requireContext(), "Tempo copiado")
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun startTimer(tvTimer: TextView, btnIniciar: Button) {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText(tvTimer)
            }

            override fun onFinish() {
                isTimerRunning = false
                btnIniciar.text = "Iniciar"
                HelperMethods.showToast(requireContext(), "Tempo esgotado!")
                sendTimerFinishedNotification()

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val currentDate = sdf.format(Date())
                val durationInMin = (initialTimeSet / 60000).toInt()
                dbHelper.addSession(durationInMin, currentDate)
            }
        }.start()

        isTimerRunning = true
        btnIniciar.text = "Pausar"
    }

    private fun pauseTimer(btnIniciar: Button?) {
        countDownTimer?.cancel()
        isTimerRunning = false
        btnIniciar?.text = "Iniciar"
    }

    private fun updateCountDownText(tvTimer: TextView?) {
        val minutes = (timeLeftInMillis / 1000).toInt() / 60
        val seconds = (timeLeftInMillis / 1000).toInt() % 60
        tvTimer?.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun sendTimerFinishedNotification() {
        createNotificationChannel()

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val vibrationPattern = longArrayOf(0, 500, 200, 500)

        val builder = NotificationCompat.Builder(requireContext(), TIMER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Minimal Timer")
            .setContentText("O tempo esgotou!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(defaultSoundUri)
            .setVibrate(vibrationPattern)
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(requireContext())) {
            try {
                notify(1001, builder.build())
            } catch (e: SecurityException) {
                // Silently fail if permission not granted
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificações do Timer"
            val descriptionText = "Avisa quando o tempo do timer chega a zero"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(TIMER_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }

    /**
     * MÓDULO 7: Verifica se há um timer salvo para a localização atual do dispositivo.
     */
    private fun checkLocationTimer(tvTimer: TextView, btnIniciar: Button) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val savedMin = dbHelper.getLocationTimer(location.latitude, location.longitude)
                    if (savedMin != null) {
                        pauseTimer(btnIniciar)
                        timeLeftInMillis = savedMin * 60000L
                        updateCountDownText(tvTimer)
                        HelperMethods.showToast(requireContext(), "📍 Timer de $savedMin min ativado por localização!")
                    }
                }
            }
        } catch (e: SecurityException) {
            // Ignora se não houver permissão
        }
    }
}
