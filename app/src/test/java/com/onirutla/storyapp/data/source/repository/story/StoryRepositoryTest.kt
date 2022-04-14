package com.onirutla.storyapp.data.source.repository.story

import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.source.api_service.StoryApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.io.File

@ExperimentalCoroutinesApi
class StoryRepositoryTest {

    private lateinit var storyRepository: StoryRepository
    private lateinit var storyApiService: StoryApiService

    private val token = "Bearer token"


    @Before
    fun setUp() {
        storyApiService = mock(StoryApiService::class.java)
        storyRepository = StoryRepositoryImpl(storyApiService)
    }

    @Test
    fun `given token getAllStoriesWithToken should not return null`() = runTest {
        val actual = storyRepository.getAllStoriesWithToken(token)


        assertNotNull(actual)
    }

    @Test
    fun `given imageMultiPart, descriptionMultiPart, token addNewStoryWithoutToken should return BaseResponse(error=false)`() =
        runTest {
            val description = "description"
            val image = File("image")
            val imageRequestBody = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val descriptionMultipart = MultipartBody.Part.createFormData(
                "description",
                description
            )
            val imageMultiPart = MultipartBody.Part.createFormData(
                "photo",
                image.name,
                imageRequestBody
            )
            `when`(
                storyApiService.addNewStoryWithToken(
                    descriptionMultipart,
                    imageMultiPart,
                    token
                )
            ).thenReturn(BaseResponse(error = false))

            val actual = storyRepository.addNewStoryWithToken(
                descriptionMultipart,
                imageMultiPart,
                token
            )

            assertEquals(BaseResponse(error = false), actual)

            verify(storyApiService).addNewStoryWithToken(
                descriptionMultipart,
                imageMultiPart,
                token
            )
        }
}