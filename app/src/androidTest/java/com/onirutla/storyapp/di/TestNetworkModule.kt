package com.onirutla.storyapp.di

import android.content.Context
import com.onirutla.storyapp.BuildConfig
import com.onirutla.storyapp.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.Charsets
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestNetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideKotlinSerialization(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Provides
    @Singleton
    fun provideKtorClient(
        @ApplicationContext context: Context,
        okHttp: OkHttpClient,
        json: Json,
    ): HttpClient = HttpClient(engineFactory = OkHttp) {
        engine { preconfigured = okHttp }
        defaultRequest { url("https://www.127.0.0.1:8080/") }
        Charsets {
            register(Charsets.UTF_8)
            sendCharset = Charsets.UTF_8
        }
        if (BuildConfig.DEBUG) {
            install(plugin = Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
        }
        install(plugin = ContentNegotiation) {
            json(json = json, contentType = ContentType.Any)
        }
        install(plugin = HttpCache) {
            privateStorage(
                FileStorage(
                    File(context.cacheDir, context.getString(R.string.app_name))
                        .apply { mkdir() }
                )
            )
        }
    }
}