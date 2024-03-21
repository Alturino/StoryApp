package com.onirutla.storyapp.auth.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.onirutla.storyapp.auth.data.UserSessionManager
import com.onirutla.storyapp.auth.data.UserSessionSerializer
import com.onirutla.storyapp.auth.data.util.UserSessionConstant
import com.onirutla.storyapp.UserSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserSessionModule {

    @Provides
    @Singleton
    fun provideUserSessionDatastore(
        @ApplicationContext context: Context,
    ): DataStore<UserSession> = DataStoreFactory.create(
        serializer = UserSessionSerializer,
        produceFile = { context.dataStoreFile(UserSessionConstant.USER_SESSION_DATASTORE_FILE) },
        corruptionHandler = ReplaceFileCorruptionHandler { UserSession.getDefaultInstance() },
        migrations = listOf(),
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    )

    @Provides
    @Singleton
    fun provideUserSessionManager(dataStore: DataStore<UserSession>): UserSessionManager =
        UserSessionManager(dataStore)

}