package com.onirutla.storyapp.ui.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.onirutla.storyapp.data.model.story.StoryResponse
import com.onirutla.storyapp.databinding.StoryItemBinding

class StoryAdapter : PagingDataAdapter<StoryResponse, ViewHolder>(Comparator) {

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
}

class ViewHolder(
    private val binding: StoryItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(storyResponse: StoryResponse) {
        binding.story = storyResponse
    }

}