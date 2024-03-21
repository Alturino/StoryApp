package com.onirutla.storyapp.auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onirutla.storyapp.auth.data.source.remote.model.request.RegisterRequest
import com.onirutla.storyapp.auth.domain.AuthRepository
import com.onirutla.storyapp.core.domain.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state: MutableStateFlow<RegisterState> = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnEmailChange -> _state.update { it.copy(email = event.email) }
            is RegisterEvent.OnPasswordChange -> _state.update { it.copy(password = event.password) }
            RegisterEvent.HandledEvent -> _state.update { it.copy(registerState = ResponseState.Initial) }
            is RegisterEvent.OnNameChange -> _state.update { it.copy(name = event.name) }
        }
    }

    fun register() {
        _state.update { it.copy(registerState = ResponseState.Loading) }
        viewModelScope.launch {
            val result = authRepository.register(
                register = RegisterRequest(
                    name = _state.value.name,
                    email = _state.value.email,
                    password = _state.value.password,
                )
            )
            Timber.d("result: $result")
            _state.update { it.copy(registerState = result) }
        }
    }

}
