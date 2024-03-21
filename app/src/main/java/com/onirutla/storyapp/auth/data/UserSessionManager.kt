package com.onirutla.storyapp.auth.data

import androidx.datastore.core.DataStore
import com.onirutla.storyapp.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionManager @Inject constructor(
    private val dataStore: DataStore<UserSession>,
) {
    val userSessionFlow: Flow<UserSession> = dataStore.data

    suspend fun updateUserSession(userSession: UserSession): UserSession = dataStore.updateData {
        it.toBuilder().apply {
            with(userSession) {
                setName(name)
                setToken(token)
                setUserId(userId)
            }
        }.build()
    }

    suspend fun getUserSession(): UserSession? = dataStore.data.firstOrNull()

}