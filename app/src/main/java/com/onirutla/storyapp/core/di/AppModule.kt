package com.onirutla.storyapp.core.di

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.onirutla.storyapp.BuildConfig
import com.onirutla.storyapp.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCoil(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
    ): ImageLoader {
        val cacheDir = File(
            context.cacheDir,
            context.getString(R.string.app_name)
        ).apply { mkdir() }

        val diskCache = DiskCache.Builder()
            .maxSizePercent(0.5)
            .directory(cacheDir)
            .build()

        val memoryCache = MemoryCache.Builder(context)
            .maxSizePercent(0.25)
            .strongReferencesEnabled(true)
            .weakReferencesEnabled(true)
            .build()

        val logger = if (BuildConfig.DEBUG) DebugLogger() else null

        return ImageLoader.Builder(context)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache { diskCache }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache { memoryCache }
            .components {
                add(SvgDecoder.Factory())
                add(VideoFrameDecoder.Factory())
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .logger(logger)
            .respectCacheHeaders(false)
            .okHttpClient(okHttpClient)
            .build()
    }

}