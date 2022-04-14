package com.onirutla.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.onirutla.storyapp.data.model.user.body.UserLoginBody
import com.onirutla.storyapp.data.model.user.response.LoginResponse
import com.onirutla.storyapp.data.source.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val loginResponse: LiveData<LoginResponse> get() = _loginResponse
    private val _loginResponse = MutableLiveData<LoginResponse>()

    val loginToken = userRepository.userToken

    fun login(userLoginBody: UserLoginBody) {
        viewModelScope.launch {
            _loginResponse.postValue(userRepository.loginUser(userLoginBody))
        }
    }

}