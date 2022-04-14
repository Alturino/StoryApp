package com.onirutla.storyapp.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.user.body.UserLoginBody
import com.onirutla.storyapp.databinding.ActivityLoginBinding
import com.onirutla.storyapp.ui.register.RegisterActivity
import com.onirutla.storyapp.ui.story.StoryActivity
import com.onirutla.storyapp.util.Constants.REGISTER_RESPONSE
import com.onirutla.storyapp.util.Util.isValidEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    private val launcherRegister =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val response = it.data?.getParcelableExtra<BaseResponse>(REGISTER_RESPONSE)
                Log.d("login", "$response")
                Toast.makeText(this, "${response?.message}", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSignUp()
        observeLoginToken()
        observeLoginResponse()
        setButtonShouldEnabled()
        setButtonClickListener()
        setEditorActionListener()
    }

    private fun setSignUp() {
        binding.signUp.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val intent = Intent(this, RegisterActivity::class.java)
        launcherRegister.launch(intent)
    }

    private fun observeLoginResponse() {
        viewModel.loginResponse.observe(this) { loginResponse ->
            loginResponse.error?.let {
                when (it) {
                    true -> Toast.makeText(this, loginResponse.message, Toast.LENGTH_SHORT).show()
                    false -> {
                        startActivity(Intent(this, StoryActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        })
                        finish()
                    }
                }
            }
        }
    }

    private fun observeLoginToken() {
        viewModel.loginToken.observe(this) {
            if (it.isNotEmpty()) {
                startActivity(Intent(this, StoryActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                })
                finish()
            }
        }
    }

    private fun setButtonShouldEnabled() {
        binding.password.addTextChangedListener {
            setButtonEnabled()
        }
    }

    private fun setButtonClickListener() {
        binding.button.setOnClickListener {
            binding.apply {
                viewModel.login(UserLoginBody(email.text.toString(), password.text.toString()))
            }
        }
    }

    private fun setEditorActionListener() {
        binding.password.setOnEditorActionListener { textView, actionId, _ ->
            if (textView.text.isNotEmpty() && textView.error.isNullOrEmpty() && binding.email.error.isNullOrEmpty()) {
                viewModel.login(
                    UserLoginBody(
                        binding.email.text.toString(),
                        textView.text.toString()
                    )
                )
                textView.closeSoftKeyboard()
            }
            actionId == EditorInfo.IME_ACTION_DONE
        }
    }

    private fun View.closeSoftKeyboard() {
        val inputMethodManager =
            this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
        this.clearFocus()
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