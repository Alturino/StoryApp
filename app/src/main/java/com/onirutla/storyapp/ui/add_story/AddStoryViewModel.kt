package com.onirutla.storyapp.ui.add_story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.data.source.repository.story.StoryRepository
import com.onirutla.storyapp.data.source.repository.user.UserRepository
import com.onirutla.storyapp.util.Util.compressImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val _image = MutableLiveData<File>()
    val image: LiveData<File> get() = _image

    private val token = userRepository.userToken

    private val _response = MutableLiveData<BaseResponse>()
    val addStoryResponse: LiveData<BaseResponse> get() = _response

    fun saveImage(image: File) {
        viewModelScope.launch {
            _image.postValue(image)
        }
    }

    fun addNewStoryWithToken(description: String, image: File) {
        viewModelScope.launch {
            val compressedImage = image.compressImage()
            val imageRequestBody = compressedImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultiPart = MultipartBody.Part.createFormData(
                "photo",
                compressedImage.name,
                imageRequestBody
            )
            val descriptionMultipart = MultipartBody.Part.createFormData("description", description)
            token.collect {
                _response.value = storyRepository.addNewStoryWithToken(
                    descriptionMultipart,
                    imageMultiPart,
                    "Bearer $token"
                )
            }
        }
    }

}