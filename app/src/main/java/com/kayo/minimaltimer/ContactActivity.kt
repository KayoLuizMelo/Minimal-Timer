package com.kayo.minimaltimer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kayo.minimaltimer.utils.HelperMethods

/**
 * MÓDULO 6: Activity para comunicação via SMS e E-mail.
 */
class ContactActivity : AppCompatActivity() {

    private val SMS_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etSmsMessage = findViewById<EditText>(R.id.etSmsMessage)
        val btnSendSmsDirect = findViewById<Button>(R.id.btnSendSmsDirect)
        val btnSendSmsIntent = findViewById<Button>(R.id.btnSendSmsIntent)

        val etEmailAddress = findViewById<EditText>(R.id.etEmailAddress)
        val etEmailSubject = findViewById<EditText>(R.id.etEmailSubject)
        val etEmailBody = findViewById<EditText>(R.id.etEmailBody)
        val btnSendEmail = findViewById<Button>(R.id.btnSendEmail)

        // 1. Enviar SMS diretamente pelo aplicativo (SmsManager)
        btnSendSmsDirect.setOnClickListener {
            val phone = etPhone.text.toString()
            val message = etSmsMessage.text.toString()

            if (phone.isNotEmpty() && message.isNotEmpty()) {
                checkSmsPermissionAndSend(phone, message)
            } else {
                HelperMethods.showToast(this, "Preencha telefone e mensagem!")
            }
        }

        // 2. Enviar SMS por meio do aplicativo de mensagens do sistema (Intent)
        btnSendSmsIntent.setOnClickListener {
            val phone = etPhone.text.toString()
            val message = etSmsMessage.text.toString()

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("sms:$phone")
            intent.putExtra("sms_body", message)
            startActivity(intent)
        }

        // 3. Enviar e-mail por meio da integração com o app de e-mail (Intent)
        btnSendEmail.setOnClickListener {
            val email = etEmailAddress.text.toString()
            val subject = etEmailSubject.text.toString()
            val body = etEmailBody.text.toString()

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                HelperMethods.showToast(this, "Nenhum app de e-mail encontrado.")
            }
        }
    }

    private fun checkSmsPermissionAndSend(phone: String, message: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_CODE)
        } else {
            sendSmsDirectly(phone, message)
        }
    }

    private fun sendSmsDirectly(phone: String, message: String) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phone, null, message, null, null)
            HelperMethods.showToast(this, "SMS enviado com sucesso!")
        } catch (e: Exception) {
            HelperMethods.showToast(this, "Falha ao enviar SMS.")
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                HelperMethods.showToast(this, "Permissão concedida. Tente enviar novamente.")
            } else {
                HelperMethods.showToast(this, "Permissão SMS negada.")
            }
        }
    }
}
