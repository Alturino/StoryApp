package com.onirutla.storyapp.story.ui.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.onirutla.storyapp.databinding.StoryItemBinding
import com.onirutla.storyapp.story.domain.data.Story

class StoryPagingAdapter(
    private inline val onClickListener: (Story) -> Unit,
) : PagingDataAdapter<Story, StoryViewHolder>(StoryDiff) {

    override fun onBindViewHolder(
        holder: StoryViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty())
            onBindViewHolder(holder, position)
        else {
            payloads.distinct().forEach {
                when (it) {
                    is StoryDiff.Payload.Name -> holder.bindName(it.value)
                    is StoryDiff.Payload.Description -> holder.bindDescription(it.value)
                    is StoryDiff.Payload.PhotoUrl -> holder.bindImage(it.value)
                    else -> {}
                }
            }
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder =
        StoryViewHolder(
            binding = StoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClickListener = onClickListener
        )
}

