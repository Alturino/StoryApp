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