package com.onirutla.storyapp.auth.data

import androidx.datastore.core.Serializer
import com.onirutla.storyapp.UserSession
import java.io.InputStream
import java.io.OutputStream


object UserSessionSerializer : Serializer<UserSession> {
    override val defaultValue: UserSession
        get() = UserSession.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserSession =
        input.use { UserSession.parseFrom(it) }

    override suspend fun writeTo(t: UserSession, output: OutputStream) =
        t.writeTo(output)
}