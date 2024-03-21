package com.onirutla.storyapp.auth.ui.login

sealed interface LoginEvent {
    data class OnEmailChange(val email: String) : LoginEvent
    data class OnPasswordChange(val password: String) : LoginEvent
    data object HandledEvent : LoginEvent
}