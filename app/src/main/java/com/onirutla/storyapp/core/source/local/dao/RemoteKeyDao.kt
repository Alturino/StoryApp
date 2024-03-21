package com.onirutla.storyapp.core.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.onirutla.storyapp.core.source.local.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Upsert
    fun upsertRemoteKey(vararg remoteKey: RemoteKeyEntity)

    @Query("select * from remote_keys where storyId = :storyId")
    @Transaction
    suspend fun getRemoteKeyByStoryId(storyId: String): RemoteKeyEntity?

    @Query("delete from remote_keys")
    suspend fun clearRemoteKey()

}