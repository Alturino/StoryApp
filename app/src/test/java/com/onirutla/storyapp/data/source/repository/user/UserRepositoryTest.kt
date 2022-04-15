package com.onirutla.storyapp.data.source.repository.user

import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.user.body.UserLoginBody
import com.onirutla.storyapp.data.model.user.body.UserRegisterBody
import com.onirutla.storyapp.data.model.user.response.LoginData
import com.onirutla.storyapp.data.model.user.response.LoginResponse
import com.onirutla.storyapp.data.source.DataStoreManager
import com.onirutla.storyapp.data.source.api_service.UserApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userApiService: UserApiService
    private lateinit var dataStoreManager: DataStoreManager

    private val email = "email@gmail.com"
    private val name = "email"
    private val password = "ini_password"
    private val loginData = LoginData()
    private val loginToken = "token"

    @Before
    fun setUp() {
        userApiService = mock(UserApiService::class.java)
        dataStoreManager = mock(DataStoreManager::class.java)
        userRepository = UserRepositoryImpl(userApiService, dataStoreManager)
    }

    @Test
    fun `given empty UserRegisterBody registerUser should return BaseResponse(error = true)`() =
        runBlockingTest {
            val emptyUserRegisterBody = UserRegisterBody()
            val actual = userRepository.registerUser(emptyUserRegisterBody)

            assertEquals(BaseResponse(error = true, message = null), actual)
        }

    @Test
    fun `given UserRegisterBody with empty name registerUser should return BaseResponse(error = true)`() =
        runBlockingTest {
            val registerBodyWithEmptyName =
                UserRegisterBody(email, password)
            val actual = userRepository.registerUser(registerBodyWithEmptyName)

            assertEquals(BaseResponse(error = true, message = null), actual)
        }

    @Test
    fun `given UserRegisterBody with empty email registerUser should return BaseResponse(error = true)`() =
        runBlockingTest {
            val registerBodyWithEmptyEmail = UserRegisterBody(name, password)
            val actual = userRepository.registerUser(registerBodyWithEmptyEmail)

            assertEquals(BaseResponse(error = true, message = null), actual)
        }

    @Test
    fun `given UserRegisterBody with empty password registerUser should return BaseResponse(error = true)`() =
        runBlockingTest {
            val registerBodyWithEmptyPassword =
                UserRegisterBody(name, email)
            val actual = userRepository.registerUser(registerBodyWithEmptyPassword)

            assertEquals(BaseResponse(error = true, message = null), actual)
        }

    @Test
    fun `given UserRegisterBody registerUser should return BaseResponse(error = false)`() =
        runBlockingTest {
            val registerBody = UserRegisterBody(name, email, password)
            `when`(userApiService.registerUser(registerBody)).thenReturn(BaseResponse(error = false))
            val actual = userRepository.registerUser(registerBody)

            assertEquals(BaseResponse(error = false), actual)

            verify(userApiService).registerUser(registerBody)
        }

    @Test
    fun `clearUserToken should be called only once when logout`() = runBlockingTest {
        userRepository.logoutUser()

        verify(dataStoreManager).clearUserToken()
    }

    @Test
    fun `given empty UserLoginBody loginUser() should return BaseResponse(error = true)`() =
        runBlockingTest {
            val emptyUserLoginBody = UserLoginBody()
            val actual = userRepository.loginUser(emptyUserLoginBody)

            assertEquals(LoginResponse(error = true, loginData, message = null), actual)
        }

    @Test
    fun `given UserLoginBody with empty email loginUser() should return BaseResponse(error = true)`() =
        runBlockingTest {
            val loginBodyWithEmptyEmail = UserLoginBody(email, password)
            val actual = userRepository.loginUser(loginBodyWithEmptyEmail)

            assertEquals(LoginResponse(error = true, loginData = loginData, message = null), actual)
        }

    @Test
    fun `given UserLoginBody with empty password loginUser() should return BaseResponse(error = true)`() =
        runBlockingTest {
            val loginBodyWithEmptyPassword = UserLoginBody(email, password)
            val actual = userRepository.loginUser(loginBodyWithEmptyPassword)

            assertEquals(LoginResponse(error = true, loginData = loginData, message = null), actual)
        }

    @Test
    fun `given UserLoginBody loginUser() should return BaseResponse(error = false)`() = runBlockingTest {
        val loginBody = UserLoginBody(email, password)
        `when`(userApiService.loginUser(loginBody)).thenReturn(LoginResponse(false, loginData))

        val actual = userRepository.loginUser(loginBody)

        assertEquals(LoginResponse(false, loginData), actual)

        verify(userApiService).loginUser(loginBody)
        verify(dataStoreManager).updateLoginToken(loginData.token!!)
    }

    @Test
    fun `given flowOf token should return token`() = runBlockingTest {
        val expected = flowOf(loginToken)
        `when`(dataStoreManager.preferenceLoginToken).thenReturn(expected)

        val actual = userRepository.userToken.first()

        assertEquals(expected.first(), actual)

        verify(dataStoreManager).preferenceLoginToken
    }

    @Test
    fun `getUserToken should only called preferenceLoginToken once`() = runBlockingTest {
        val expected = loginToken
        `when`(dataStoreManager.preferenceLoginToken).thenReturn(flowOf(loginToken))

        val actual = userRepository.getUserToken()

        assertEquals(expected, actual)

        verify(dataStoreManager).preferenceLoginToken
    }


    @Test
    fun `updateLoginToken should only called once`() = runBlockingTest {
        userRepository.setUserToken(loginToken)

        verify(dataStoreManager).updateLoginToken(loginToken)
    }
}