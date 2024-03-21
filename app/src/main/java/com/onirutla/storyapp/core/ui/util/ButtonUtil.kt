package com.onirutla.storyapp.core.ui.util

import android.view.View
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun View.clicks(): Flow<Unit> = callbackFlow {
    setOnClickListener { trySend(Unit) }
    awaitClose { setOnClickListener(null) }
}