package com.onirutla.storyapp.story.ui.component

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.onirutla.storyapp.databinding.StoryItemBinding
import com.onirutla.storyapp.story.domain.data.Story

class StoryViewHolder(
    private val binding: StoryItemBinding,
    private inline val onClickListener: (Story) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(story: Story) {
        binding.apply {
            root.setOnClickListener { onClickListener(story) }
            with(story) {
                bindName(name)
                bindDescription(description)
                bindImage(photoUrl)
            }
        }
    }

    fun bindName(name: String) {
        binding.tvSender.text = name
    }

    fun bindDescription(description: String) {
        binding.tvDescription.text = description
    }

    fun bindImage(url: String) {
        binding.ivStory.load(url)
    }
}