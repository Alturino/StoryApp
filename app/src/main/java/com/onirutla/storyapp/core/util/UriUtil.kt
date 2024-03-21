package com.onirutla.storyapp.core.util

import android.net.Uri

fun Uri?.orEmpty(): Uri = this ?: Uri.EMPTY