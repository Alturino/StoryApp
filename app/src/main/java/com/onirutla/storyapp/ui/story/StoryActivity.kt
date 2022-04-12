package com.onirutla.storyapp.ui.story

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.onirutla.storyapp.R
import com.onirutla.storyapp.data.model.BaseResponse
import com.onirutla.storyapp.databinding.ActivityStoryBinding
import com.onirutla.storyapp.ui.add_story.AddStoryActivity
import com.onirutla.storyapp.ui.login.LoginActivity
import com.onirutla.storyapp.util.Constants.ADD_STORY_RESPONSE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

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

        viewModel.stories.observe(this) {
            storyAdapter.submitData(lifecycle, it)
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

    override fun onResume() {
        super.onResume()
        if (response != null && response?.error == false) {
            viewModel.getNewestStory()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                viewModel.logout()
                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                )
            }
            R.id.change_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startAddActivity() {
        val intent = Intent(this, AddStoryActivity::class.java)
        launcherAddStory.launch(intent)
    }

}