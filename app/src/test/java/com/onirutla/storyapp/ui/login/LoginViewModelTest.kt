package com.onirutla.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.onirutla.storyapp.MainCoroutineRule
import com.onirutla.storyapp.data.model.user.body.UserLoginBody
import com.onirutla.storyapp.data.model.user.response.LoginResponse
import com.onirutla.storyapp.data.source.repository.user.UserRepository
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var userRepository: UserRepository
    private lateinit var loginViewModel: LoginViewModel

    private val token = "token"

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        loginViewModel = LoginViewModel(userRepository)
    }

    @Test
    fun test() = mainCoroutineRule.runBlockingTest {
        val loginBody = UserLoginBody()
        val expected = LoginResponse()
        `when`(userRepository.loginUser(loginBody)).thenReturn(expected)
        `when`(userRepository.userToken).thenReturn(flowOf(token))

        loginViewModel.login(loginBody)

        val actual = loginViewModel.loginResponse.getOrAwaitValue()

        Assert.assertEquals(expected, actual)

        verify(userRepository).loginUser(loginBody)
    }

}