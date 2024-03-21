package com.onirutla.storyapp.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onirutla.storyapp.auth.data.source.remote.model.request.LoginRequest
import com.onirutla.storyapp.auth.domain.AuthRepository
import com.onirutla.storyapp.auth.domain.data.Login
import com.onirutla.storyapp.core.domain.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        _state.onEach { Timber.d("state: $it") }
            .map { it.loginState }
            .onEach { Timber.d("Mapped: $it") }
            .distinctUntilChanged()
            .filterIsInstance<ResponseState.Success<Login>>()
            .onEach { Timber.d("Filtered: $it") }
            .onEach { authRepository.updateUserSession(it.data) }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChange -> _state.update { it.copy(email = event.email) }
            is LoginEvent.OnPasswordChange -> _state.update { it.copy(password = event.password) }
            LoginEvent.HandledEvent -> _state.update { it.copy(loginState = ResponseState.Initial) }
        }
    }

    fun login() {
        _state.update { it.copy(loginState = ResponseState.Loading) }
        viewModelScope.launch {
            _state.update { it.copy(loginState = ResponseState.Loading) }
            val result = authRepository.login(
                login = LoginRequest(
                    email = _state.value.email,
                    password = _state.value.password,
                )
            )
            Timber.d("result: $result")
            _state.update { it.copy(loginState = result) }
        }
    }

}
