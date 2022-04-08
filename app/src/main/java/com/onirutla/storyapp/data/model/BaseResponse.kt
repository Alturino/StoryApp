package com.onirutla.storyapp.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class BaseResponse(
    @Json(name = "error")
    val error: Boolean? = false,
    @Json(name = "message")
    val message: String? = ""
): Parcelable
