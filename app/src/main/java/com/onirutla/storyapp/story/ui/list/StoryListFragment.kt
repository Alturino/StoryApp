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

package com.onirutla.storyapp.story.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.onirutla.storyapp.R
import com.onirutla.storyapp.core.util.isOnline
import com.onirutla.storyapp.databinding.FragmentStoryListBinding
import com.onirutla.storyapp.story.domain.data.StoryFilterType
import com.onirutla.storyapp.story.domain.data.StorySortType
import com.onirutla.storyapp.story.ui.component.StoryPagingAdapter
import com.onirutla.storyapp.story.ui.story_map.StoryMapActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class StoryListFragment : Fragment() {

    private var _binding: FragmentStoryListBinding? = null
    private val binding: FragmentStoryListBinding
        get() = _binding!!

    private val vm: StoryListViewModel by viewModels()

    private val pagingAdapter = StoryPagingAdapter {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            toolbar.apply {
                inflateMenu(R.menu.story_list_menu)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.filter_all -> {
                            vm.onEvent(StoryListEvent.OnFilterChange(StoryFilterType.All))
                            true
                        }

                        R.id.filter_with_location -> {
                            vm.onEvent(StoryListEvent.OnFilterChange(StoryFilterType.WithLocation))
                            true
                        }

                        R.id.filter_without_location -> {
                            vm.onEvent(StoryListEvent.OnFilterChange(StoryFilterType.WithoutLocation))
                            true
                        }

                        R.id.sort_earliest -> {
                            vm.onEvent(StoryListEvent.OnSortChange(StorySortType.CreatedAtAscending))
                            true
                        }

                        R.id.sort_latest -> {
                            vm.onEvent(StoryListEvent.OnSortChange(StorySortType.CreatedAtDescending))
                            true
                        }

                        R.id.sort_name_ascending -> {
                            vm.onEvent(StoryListEvent.OnSortChange(StorySortType.NameAscending))
                            true
                        }

                        R.id.sort_name_descending -> {
                            vm.onEvent(StoryListEvent.OnSortChange(StorySortType.NameDescending))
                            true
                        }

                        R.id.story_map -> {
                            Intent(requireActivity(), StoryMapActivity::class.java)
                                .also { startActivity(it) }
                            true
                        }

                        else -> false
                    }
                }
            }
            rvStory.apply {
                adapter = pagingAdapter
                setHasFixedSize(true)
            }
        }

        requireContext().isOnline()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach { Timber.d("isOnline: $it") }
            .onEach { vm.onEvent(StoryListEvent.OnOnlineChange(it)) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.fab.setOnClickListener {
            findNavController().navigate(StoryListFragmentDirections.actionFragmentStoryListToFragmentAddStory())
        }

        vm.stories.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach { pagingAdapter.submitData(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}