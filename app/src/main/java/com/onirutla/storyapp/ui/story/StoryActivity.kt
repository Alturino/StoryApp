package com.onirutla.storyapp.ui.story

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.onirutla.storyapp.databinding.ActivityStoryBinding

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}