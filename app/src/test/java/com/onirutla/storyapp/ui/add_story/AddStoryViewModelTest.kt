package com.onirutla.storyapp.ui.add_story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.onirutla.storyapp.MainCoroutineRule
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.source.repository.story.StoryRepository
import com.onirutla.storyapp.data.source.repository.user.UserRepository
import getOrAwaitValue
import com.onirutla.storyapp.util.Util
import com.onirutla.storyapp.util.Util.compressImage
import com.onirutla.storyapp.util.Util.toMultipart
import com.onirutla.storyapp.util.Util.toRequestBody
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import java.io.File

@ExperimentalCoroutinesApi
class AddStoryViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var storyRepository: StoryRepository
    private lateinit var userRepository: UserRepository

    private val description = "description"
    private val image = File("screenshot.png")
    private val token = "token"

    @Before
    fun setUp() {
        storyRepository = mock(StoryRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        addStoryViewModel = AddStoryViewModel(storyRepository, userRepository)
    }

    @Test
    fun `given valid argument addNewStoryToken should return BaseResponse and not null`() = mainCoroutineRule.runBlockingTest {
        mockStatic(Util::class.java)
        val expected = BaseResponse()
        val descriptionMultipart = MultipartBody.Part.createFormData("description", description)
        val imageRequestBody = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        `when`(image.toRequestBody()).thenReturn(imageRequestBody)
        val imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            image.name,
            imageRequestBody
        )
        `when`(image.compressImage()).thenReturn(image)
        `when`(description.toMultipart()).thenReturn(descriptionMultipart)
        `when`(image.toMultipart()).thenReturn(imageMultipart)
        `when`(
            storyRepository.addNewStoryWithToken(
                descriptionMultipart,
                imageMultipart,
                "Bearer $token"
            )
        ).thenReturn(expected)
        `when`(userRepository.getUserToken()).thenReturn(token)

        addStoryViewModel.addNewStoryWithToken(description, image)

        val actual = addStoryViewModel.addStoryResponse.getOrAwaitValue()

        assertEquals(expected, actual)
        assertNotNull(actual)

        verify(userRepository).getUserToken()
        verify(storyRepository).addNewStoryWithToken(descriptionMultipart, imageMultipart, "Bearer $token")
    }

    @Test
    fun `given image livedata image should not null when saveImage called`(){
        val expected = image
        addStoryViewModel.saveImage(expected)

        val actual = addStoryViewModel.image.getOrAwaitValue()

        assertEquals(expected, actual)
        assertNotNull(actual)
    }

}