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

package com.onirutla.storyapp.story.ui.story_map

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.markerClickEvents
import com.onirutla.storyapp.R
import com.onirutla.storyapp.core.ui.util.clicks
import com.onirutla.storyapp.databinding.ActivityStoryMapBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoryMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryMapBinding

    private val vm: StoryMapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        BottomSheetBehavior.from(binding.btsStory).apply {
            isHideable = true
            peekHeight = 100
        }

        val state = vm.state.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)

        binding.btnNext.clicks()
            .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .onEach { vm.onEvent(StoryMapEvent.OnStoryNext) }
            .launchIn(lifecycleScope)

        binding.btnPrev.clicks()
            .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .onEach { vm.onEvent(StoryMapEvent.OnStoryPrev) }
            .launchIn(lifecycleScope)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.fcv_story_map) as SupportMapFragment
                val googleMap = mapFragment.awaitMap()

                googleMap.markerClickEvents()
                    .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                    .onEach { vm.onEvent(StoryMapEvent.OnMarkerClick(it)) }
                    .launchIn(this@launch)

                state.filterNot { it.stories.isEmpty() }
                    .map { it.stories }
                    .distinctUntilChanged()
                    .onEach { stories ->
                        stories.forEach {
                            googleMap.addMarker {
                                title(it.name)
                                position(LatLng(it.lat, it.lon))
                                draggable(false)
                            }
                        }
                    }
                    .launchIn(this@launch)

                state.map { it.currentStory }
                    .distinctUntilChanged()
                    .onEach {
                        with(it) {
                            binding.apply {
                                tvSender.text = name
                                tvDescription.text = description
                                ivStory.load(photoUrl)
                            }
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 10f)
                            )
                        }
                    }
                    .launchIn(this@launch)
            }
        }
    }
}