package com.onirutla.storyapp.data.source.repository.story

import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.model.PageResponse
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.data.source.api_service.StoryApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
    private val image = File("screenshot.png")
    private val description = "description"
    private val imageRequestBody = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
    private val descriptionMultipart = MultipartBody.Part.createFormData(
        "description",
        description
    )
    private val imageMultiPart = MultipartBody.Part.createFormData(
        "photo",
        image.name,
        imageRequestBody
    )


    @Before
    fun setUp() {
        storyApiService = mock(StoryApiService::class.java)
        storyRepository = StoryRepositoryImpl(storyApiService)
    }

    @Test
    fun `given token getAllStoriesWithToken should not return null`() = runBlockingTest {
        val actual = storyRepository.getAllStoriesWithToken(token)

        assertNotNull(actual)
    }

    @Test
    fun `given imageMultiPart, descriptionMultiPart, token addNewStoryWithoutToken should return BaseResponse(error=false)`() =
        runBlockingTest {

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

    @Test
    fun `given exception addNewStoryWithToken should return BaseResponse(error=true)`() =
        runBlockingTest {
            `when`(
                storyApiService.addNewStoryWithToken(
                    descriptionMultipart,
                    imageMultiPart,
                    token
                )
            ).thenThrow(IllegalArgumentException())

            val actual =
                storyRepository.addNewStoryWithToken(descriptionMultipart, imageMultiPart, token)

            assertEquals(BaseResponse(error = true, actual.message), actual)

            verify(storyApiService).addNewStoryWithToken(
                descriptionMultipart,
                imageMultiPart,
                token
            )
        }

    @Test
    fun `given BaseResponse(error = true) addNewStoryWithToken should return BaseResponse(error = true)`() =
        runBlockingTest {
            `when`(
                storyApiService.addNewStoryWithToken(
                    descriptionMultipart,
                    imageMultiPart,
                    token
                )
            ).thenReturn(
                BaseResponse(error = true)
            )

            val actual =
                storyRepository.addNewStoryWithToken(descriptionMultipart, imageMultiPart, token)

            assertEquals(BaseResponse(error = true), actual)

            verify(storyApiService).addNewStoryWithToken(
                descriptionMultipart,
                imageMultiPart,
                token
            )
        }

    @Test
    fun `given exception getStoriesWithTokenAndLocation should return PageResponse(error = true)`() =
        runBlockingTest {
            `when`(storyApiService.getStoriesWithTokenAndLocation(token = "Bearer $token")).thenThrow(
                IllegalArgumentException()
            )

            val actual = storyRepository.getStoriesWithTokenAndLocation(token)

            assertEquals(
                PageResponse<StoryResponse>(error = true, emptyList(), actual.message),
                actual
            )

            verify(storyApiService).getStoriesWithTokenAndLocation(token = "Bearer $token")
        }
}