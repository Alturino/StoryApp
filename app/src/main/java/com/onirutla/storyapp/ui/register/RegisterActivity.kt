package com.onirutla.storyapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.onirutla.storyapp.data.model.user.body.UserRegisterBody
import com.onirutla.storyapp.databinding.ActivityRegisterBinding
import com.onirutla.storyapp.util.Constants.REGISTER_RESPONSE
import com.onirutla.storyapp.util.Util.isValidEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonShouldEnabled()
        setSubmitButton()
        observeResponse()
    }

    private fun setSubmitButton() {
        binding.button.setOnClickListener {
            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            lifecycleScope.launch {
                viewModel.register(UserRegisterBody(username, email, password))
            }
        }
    }

    private fun setButtonShouldEnabled() {
        binding.apply {
            username.addTextChangedListener {
                setButtonEnabled()
            }
            email.addTextChangedListener {
                setButtonEnabled()
            }
            password.addTextChangedListener {
                setButtonEnabled()
            }
        }
    }

    private fun setButtonEnabled() {
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()
        val email = binding.email.text.toString()
        val buttonShouldEnabled = (password
            .isNotEmpty() && password.length >= 6) && (email
            .isNotEmpty() && email.isValidEmail() && username.isNotEmpty())
        binding.button.isEnabled = buttonShouldEnabled
    }

    private fun observeResponse() {
        viewModel.response.observe(this) {
            if (it.error == false) {
                Toast.makeText(this, "${it.message}", LENGTH_SHORT).show()
                Log.d("error false", "${it.message}")
                setResult(RESULT_OK, Intent().putExtra(REGISTER_RESPONSE, it))
                finishAndRemoveTask()
            }
            if(it.error == true){
                setResult(RESULT_OK, Intent().putExtra(REGISTER_RESPONSE, it))
                Log.d("error true", "${it.message}")
                Toast.makeText(this, "${it.message}", LENGTH_SHORT).show()
            }
        }
    }
}