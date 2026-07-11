package com.kayo.minimaltimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

        // Encontra o botão de ajuda no XML
        val btnAjuda = view.findViewById<Button>(R.id.btnAjuda)

        // Quando clicar no botão, mostra a caixinha de diálogo (AlertDialog)
        btnAjuda.setOnClickListener {
            val construtorDialogo = AlertDialog.Builder(requireContext())
            construtorDialogo.setTitle("Minimal Timer")
            construtorDialogo.setMessage("Clique em Iniciar para começar a contagem de 25 minutos do seu foco!")
            construtorDialogo.setPositiveButton("Entendi", null)

            // Mostra o aviso na tela
            construtorDialogo.show()
        }

        return view
    }
}