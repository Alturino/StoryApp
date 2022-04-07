package com.onirutla.storyapp.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.onirutla.storyapp.util.isValidEmail
import com.onirutla.storyapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.password.addTextChangedListener {
            setButtonEnabled()
        }
    }

    private fun setButtonEnabled() {
        val password = binding.password.text
        val email = binding.email.text
        val buttonShouldEnabled = (password.toString()
            .isNotEmpty() && password.toString().length >= 6) && (email.toString()
            .isNotEmpty() && email.toString().isValidEmail())
        binding.button.isEnabled = buttonShouldEnabled
    }


}