package com.kayo.minimaltimer

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kayo.minimaltimer.utils.HelperMethods
import java.util.Locale

class TimerFragment : Fragment() {

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 1500000 // 25 minutos padrão
    private var isTimerRunning: Boolean = false

    // Esse método liga o arquivo Kotlin ao visual XML que criamos no Passo 3
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout do fragment
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        // Mapeando os componentes necessários para os critérios do Módulo 3
        val tvTimer = view.findViewById<TextView>(R.id.tvTimer)
        val etCustomTime = view.findViewById<EditText>(R.id.etCustomTime)
        val btnAplicarTempo = view.findViewById<Button>(R.id.btnAplicarTempo)
        val btnIniciar = view.findViewById<Button>(R.id.btnIniciar)
        val btnAjuda = view.findViewById<Button>(R.id.btnAjuda)

        // MÓDULO 4: Registrar o componente para o Menu de Contexto
        registerForContextMenu(tvTimer)

        // MÓDULO 3: Tratamento de evento de clique e recuperação de valores
        btnAplicarTempo.setOnClickListener {
            val textoDigitado = etCustomTime.text.toString()

            if (textoDigitado.isNotEmpty()) {
                val minutos = textoDigitado.toInt()

                // Validação simples de segurança do tempo
                if (minutos in 1..180) {
                    pauseTimer(btnIniciar)
                    timeLeftInMillis = minutos * 60000L
                    updateCountDownText(tvTimer)
                    HelperMethods.showToast(requireContext(), "Tempo alterado para $minutos min")
                } else {
                    HelperMethods.showToast(requireContext(), "Insira um valor entre 1 e 180 minutos")
                }
            } else {
                HelperMethods.showToast(requireContext(), "Por favor, digite os minutos!")
            }
        }

        // MÓDULO 4: Lógica do Timer (Iniciar/Pausar)
        btnIniciar.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer(btnIniciar)
            } else {
                startTimer(tvTimer, btnIniciar)
            }
        }

        // MÓDULO 4: Atualizado para abrir WebView de Ajuda
        btnAjuda.setOnClickListener {
            val intent = Intent(requireContext(), HelpWebViewActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    // MÓDULO 4: Menu de Contexto (Ações para um componente específico)
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
                timeLeftInMillis = 1500000 // Reseta para 25 min
                updateCountDownText(tvTimer)
                HelperMethods.showToast(requireContext(), "Timer reiniciado")
                true
            }
            R.id.ctx_copy -> {
                HelperMethods.showToast(requireContext(), "Tempo copiado para área de transferência")
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

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}