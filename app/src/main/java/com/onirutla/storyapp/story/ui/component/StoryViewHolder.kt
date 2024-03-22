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