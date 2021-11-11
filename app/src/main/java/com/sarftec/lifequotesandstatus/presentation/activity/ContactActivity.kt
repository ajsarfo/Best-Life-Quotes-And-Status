package com.sarftec.lifequotesandstatus.presentation.activity

import android.content.Intent
import android.os.Bundle
import com.sarftec.lifequotesandstatus.databinding.ActivityContactBinding
import com.sarftec.lifequotesandstatus.presentation.sound.SoundManager
import com.sarftec.lifequotesandstatus.presentation.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.lang.StringBuilder

@AndroidEntryPoint
class ContactActivity : BaseActivity() {

    private val binding by lazy {
        ActivityContactBinding.inflate(
            layoutInflater
        )
    }

    override fun canShowInterstitial(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            dependency.playSound(SoundManager.Sound.TAP)
            super.onBackPressed()
        }
        setupContactForm()
    }

    private fun checkFields(): Boolean {
        var isOkay = true
        binding.apply {
            when {
                etName.text.isNullOrEmpty() -> {
                    toast("Failed: Name field is empty!")
                    isOkay = false
                }
                etEmail.text.isNullOrEmpty() -> {
                    toast("Failed: Email field is empty!")
                    isOkay = false
                }
                etMessage.text.isNullOrEmpty() -> {
                    toast("Failed: Message field is empty!")
                    isOkay = false
                }
            }
        }
        return isOkay
    }

    private fun setupContactForm() {
        binding.cvSubmit.setOnClickListener {
            dependency.playSound(SoundManager.Sound.FAVORITE)
            binding.apply {
                if (!checkFields()) return@setOnClickListener
                val name = etName.text!!.toString()
                val email = etEmail.text!!.toString()
                val number = etWaNo.text?.toString()
                val message = etMessage.text!!.toString()

                val body = StringBuilder()
                body.append(message)
                body.append("\n")
                body.append("Email at : ")
                body.append(email)
                body.append("\n")
               if(!number.isNullOrEmpty()) {
                   val caption = "Available on Whatsapp : "
                   body.append(caption.uppercase())
                   body.append(it)
               }
                sendMail("Message from ${name.uppercase()}", body.toString())
            }
        }
    }

    private fun sendMail(subject: String, text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf("kakrajoe123@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
            type = "message/rfc822"
        }
        startActivity(Intent.createChooser(intent, "Choose and email client:"))
    }
}