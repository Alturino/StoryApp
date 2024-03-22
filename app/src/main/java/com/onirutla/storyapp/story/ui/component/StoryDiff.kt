/*
 * MIT License
 *
 * Copyright (c) 2023 Ricky Alturino
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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