package com.onirutla.storyapp.story.ui.component

import androidx.recyclerview.widget.DiffUtil
import com.onirutla.storyapp.story.domain.data.Story

object StoryDiff : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
        oldItem == newItem

    override fun getChangePayload(oldItem: Story, newItem: Story): Payload? {
        return when {
            oldItem.name != newItem.name -> Payload.Name(newItem.name)
            oldItem.description != newItem.description -> Payload.Description(newItem.description)
            oldItem.photoUrl != newItem.photoUrl -> Payload.PhotoUrl(newItem.photoUrl)
            else -> null
        }
    }

    sealed interface Payload {
        data class Name(val value: String) : Payload
        data class Description(val value: String) : Payload
        data class PhotoUrl(val value: String) : Payload
    }
}