package com.onirutla.storyapp.auth.ui.register

sealed interface RegisterEvent {
    data class OnEmailChange(val email: String) : RegisterEvent
    data class OnNameChange(val name: String) : RegisterEvent
    data class OnPasswordChange(val password: String) : RegisterEvent
    data object HandledEvent : RegisterEvent
}