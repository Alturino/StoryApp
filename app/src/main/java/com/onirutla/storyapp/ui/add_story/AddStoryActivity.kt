package com.onirutla.storyapp.ui.add_story

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.onirutla.storyapp.databinding.ActivityAddStoryBinding
import com.onirutla.storyapp.util.Constants.ADD_STORY_RESPONSE
import com.onirutla.storyapp.util.Util.createTempFile
import com.onirutla.storyapp.util.Util.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    private lateinit var currentPhotoPath: String

    private val viewModel: AddStoryViewModel by viewModels()

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val photo = File(currentPhotoPath)
                viewModel.saveImage(photo)
            }
        }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val uri = it.data?.data as Uri
                val photo = uri.uriToFile(this)
                viewModel.saveImage(photo)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        viewModel.image.observe(this) { file ->
            binding.apply {
                previewImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
                uploadButton.isEnabled = file != null
                uploadButton.setOnClickListener {
                    val description = binding.descriptionEditText.text.toString()
                    viewModel.addNewStoryWithToken(
                        description,
                        file
                    )
                }
            }
        }

        viewModel.addStoryResponse.observe(this@AddStoryActivity) {
            if (it.error == false && it != null) {
                setResult(RESULT_OK, Intent().putExtra(ADD_STORY_RESPONSE, it))
                finishAndRemoveTask()
            }
        }

    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = ACTION_GET_CONTENT
            type = "image/*"
        }

        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            resolveActivity(packageManager)
        }

        applicationContext.createTempFile().also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.onirutla.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }
}