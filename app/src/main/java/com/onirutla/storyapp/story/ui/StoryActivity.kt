package com.onirutla.storyapp.story.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.onirutla.storyapp.R
import com.onirutla.storyapp.databinding.ActivityStoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding

    private lateinit var navController: NavController

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionToGrant ->
        permissionToGrant.entries
            .forEach { Timber.d("Permission ${it.key} isGranted: ${it.value}") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.activityStory) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.story_nav_host_fragment) as NavHostFragment

        navController = navHostFragment.findNavController()

        navController.currentBackStackEntryFlow
            .flowWithLifecycle(lifecycle)
            .onEach { Timber.d("$it") }
            .launchIn(lifecycleScope)

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}