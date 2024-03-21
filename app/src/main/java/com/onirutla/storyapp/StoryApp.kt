package com.onirutla.storyapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class StoryApp : Application(), ImageLoaderFactory, Configuration.Provider {

    @Inject
    lateinit var coil: ImageLoader

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun newImageLoader(): ImageLoader = coil

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}