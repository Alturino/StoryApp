package com.onirutla.storyapp.core.ui.util

import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

fun EditText.asFlow(): Flow<String> = callbackFlow {
    val textWatcher = doAfterTextChanged {
        trySend(it.toString())
    }
    awaitClose { removeTextChangedListener(textWatcher) }
}.distinctUntilChanged()