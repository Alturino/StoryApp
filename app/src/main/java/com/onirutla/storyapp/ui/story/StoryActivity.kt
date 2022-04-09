package com.onirutla.storyapp.ui.story

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.databinding.ActivityStoryBinding
import com.onirutla.storyapp.ui.add_story.AddStoryActivity
import com.onirutla.storyapp.util.Constants.ADD_STORY_RESPONSE
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
    private var response: BaseResponse? = null

    private val launcherAddStory =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                response = it.data?.getParcelableExtra(ADD_STORY_RESPONSE)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                if (response != null && response?.error == false) {
                    viewModel.getNewestStory()
                }
                viewModel.stories.collect {
                    storyAdapter.submitData(it)
                }
            }
        }

        binding.addStoryButton.setOnClickListener {
            startAddActivity()
        }

        binding.storyList.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@StoryActivity)
            setHasFixedSize(true)
        }
    }

    private fun startAddActivity() {
        val intent = Intent(this, AddStoryActivity::class.java)
        launcherAddStory.launch(intent)
    }

}