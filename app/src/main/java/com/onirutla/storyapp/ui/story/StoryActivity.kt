package com.onirutla.storyapp.ui.story

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.onirutla.storyapp.databinding.ActivityStoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding

    private val viewModel: StoryViewModel by viewModels()

    private val storyAdapter: StoryAdapter by lazy { StoryAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stories.collect {
                    storyAdapter.submitData(it)
                }
            }
        }

        binding.storyList.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@StoryActivity)
            setHasFixedSize(true)
        }
    }
}