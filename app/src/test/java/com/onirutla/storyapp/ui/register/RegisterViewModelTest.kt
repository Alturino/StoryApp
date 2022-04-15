package com.onirutla.storyapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.onirutla.storyapp.MainCoroutineRule
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.user.body.UserRegisterBody
import com.onirutla.storyapp.data.source.repository.user.UserRepository
import com.onirutla.storyapp.data.source.repository.user.UserRepositoryImpl
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
class RegisterViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setUp() {
        userRepository = mock(UserRepositoryImpl::class.java)
        registerViewModel = RegisterViewModel(userRepository)
    }

    @Test
    fun `given empty UserRegisterBody should return BaseResponse(error = false)`() =
        mainCoroutineRule.runBlockingTest {
            val registerBody = UserRegisterBody()
            val expected = BaseResponse(error = false)
            `when`(userRepository.registerUser(registerBody)).thenReturn(expected)

            registerViewModel.register(registerBody)

            val actual = registerViewModel.response.getOrAwaitValue()

            assertEquals(expected, actual)

            verify(userRepository).registerUser(registerBody)
        }

}