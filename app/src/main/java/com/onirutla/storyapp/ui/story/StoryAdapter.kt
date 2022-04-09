package com.onirutla.storyapp.ui.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.databinding.StoryItemBinding
import com.onirutla.storyapp.ui.detail.DetailActivity

class StoryAdapter : PagingDataAdapter<StoryResponse, StoryAdapter.ViewHolder>(Comparator) {

    private object Comparator : DiffUtil.ItemCallback<StoryResponse>() {
        override fun areItemsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean =
            oldItem.description == newItem.description
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position) ?: StoryResponse())

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        binding = StoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    inner class ViewHolder(
        private val binding: StoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(storyResponse: StoryResponse) {
            binding.story = storyResponse

            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java).putExtra(
                    "story",
                    storyResponse
                )

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    it.context as Activity,
                    Pair(binding.storyImage, "story_image"),
                    Pair(binding.storyName, "story_name"),
                    Pair(binding.storyDescription, "story_description")
                ).toBundle()

                it.context.startActivity(intent, optionsCompat)
            }

        }

    }
}