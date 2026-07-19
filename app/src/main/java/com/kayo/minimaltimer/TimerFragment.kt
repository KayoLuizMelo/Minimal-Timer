package com.kayo.minimaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class TimerFragment : Fragment() {

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

        // MÓDULO 3: Tratamento de evento de clique e recuperação de valores
        btnAplicarTempo.setOnClickListener {
            val textoDigitado = etCustomTime.text.toString()

            if (textoDigitado.isNotEmpty()) {
                val minutos = textoDigitado.toInt()

                // Validação simples de segurança do tempo
                if (minutos in 1..180) {
                    tvTimer.text = String.format("%02d:00", minutos)
                    Toast.makeText(requireContext(), "Tempo alterado para $minutos min", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Insira um valor entre 1 e 180 minutos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, digite os minutos!", Toast.LENGTH_SHORT).show()
            }
        }

        // Evento de clique para o botão Iniciar (Lógica a ser implementada no Módulo 4)
        btnIniciar.setOnClickListener {
            Toast.makeText(requireContext(), "O timer ainda não inicia, funcionalidade do Módulo 4!", Toast.LENGTH_SHORT).show()
        }

        // Quando clicar no botão, mostra a caixinha de diálogo (AlertDialog original mantido)
        btnAjuda.setOnClickListener {
            val construtorDialogo = AlertDialog.Builder(requireContext())
            construtorDialogo.setTitle("Minimal Timer")
            // Atualizei levemente a mensagem para orientar sobre a nova caixa de texto
            construtorDialogo.setMessage("Digite os minutos que desejar no campo, clique em 'Definir Tempo' e depois use o botão Iniciar!")
            construtorDialogo.setPositiveButton("Entendi", null)

            // Mostra o aviso na tela
            construtorDialogo.show()
        }

        return view
    }
}