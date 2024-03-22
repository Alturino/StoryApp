/*
 * MIT License
 *
 * Copyright (c) 2023 Ricky Alturino
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.onirutla.storyapp.story.ui.add_story

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.load
import com.onirutla.storyapp.R
import com.onirutla.storyapp.core.ui.util.asFlow
import com.onirutla.storyapp.core.ui.util.clicks
import com.onirutla.storyapp.core.util.WorkerTag
import com.onirutla.storyapp.core.util.latAndLonFlow
import com.onirutla.storyapp.core.util.orEmpty
import com.onirutla.storyapp.databinding.FragmentAddStoryBinding
import com.onirutla.storyapp.story.data.StoryUploadWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding: FragmentAddStoryBinding
        get() = _binding!!

    private val vm: AddStoryViewModel by viewModels()

    private val photoPickerLauncher = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null || uri != Uri.EMPTY) {
            Timber.d("Photo selected with uri: ${uri.toString()}")
            vm.onEvent(AddStoryEvent.OnUriChange(uri.orEmpty()))
        } else {
            Timber.d("No photo selected")
        }
    }

    private val cameraLauncher = registerForActivityResult(TakePicture()) {
        if (it) {
            Timber.d("Image was saved to the given uri")
        } else {
            Timber.d("Image saving failed to the given uri")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            owner = this,
            onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCamera.clicks()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach {
                val dateTime = LocalDateTime.now()
                val dateString = DateTimeFormatter.ISO_DATE.format(dateTime)
                val dateTimeString = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime)
                val photoExtension = ".jpg"

                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DATE_ADDED, dateString)
                    put(MediaStore.Images.Media.DATE_MODIFIED, dateString)
                    put(MediaStore.Images.Media.DATE_TAKEN, dateString)
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$dateTimeString$photoExtension")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_name)
                    )
                    put(MediaStore.Images.Media.TITLE, "$dateTimeString$photoExtension")
                    put(MediaStore.Images.Media.YEAR, dateTime.year)
                }
                val contentResolver = requireContext().contentResolver
                val resultUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                vm.onEvent(AddStoryEvent.OnUriChange(resultUri.orEmpty()))
                cameraLauncher.launch(resultUri)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.btnGallery.clicks()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach { photoPickerLauncher.launch(PickVisualMediaRequest(mediaType = PickVisualMedia.ImageOnly)) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        requireContext().latAndLonFlow()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .catch { Timber.e(it) }
            .onEach { Timber.d("lat: ${it.first}, lon: ${it.second}") }
            .onEach {
                vm.onEvent(AddStoryEvent.OnLatChange(it.first))
                vm.onEvent(AddStoryEvent.OnLonChange(it.second))
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        val state = vm.state
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach { Timber.d("state: $it") }

        state.map { it.photoUri }
            .filterNot { it == Uri.EMPTY }
            .onEach { binding.apply { ivPreview.load(it) } }
            .catch { Timber.e(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        val workManager = WorkManager.getInstance(requireContext())
        val workerBuilder = OneTimeWorkRequestBuilder<StoryUploadWorker>()
            .setConstraints(
                constraints = Constraints(
                    requiredNetworkType = NetworkType.UNMETERED,
                    requiresCharging = false,
                    requiresBatteryNotLow = true,
                    requiresDeviceIdle = false,
                )
            ).addTag(WorkerTag.UPLOAD_STORY_WORKER_TAG)

        binding.apply {
            btnSubmit.clicks()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
                .flatMapLatest { state }
                .filterNot { it.description.isEmpty() or it.description.isBlank() }
                .onEach {
                    val workData = workDataOf(
                        "description" to it.description,
                        "lat" to it.lat,
                        "lon" to it.lon,
                        "photo" to it.photoUri.toString(),
                    )
                    workerBuilder.setInputData(workData)
                    workManager.enqueue(workerBuilder.build())
                    findNavController().navigateUp()
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)

            etDescription.asFlow()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .onEach { vm.onEvent(AddStoryEvent.OnDescriptionChange(it)) }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}