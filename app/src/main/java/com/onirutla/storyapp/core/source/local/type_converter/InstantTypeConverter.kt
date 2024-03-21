package com.onirutla.storyapp.core.source.local.type_converter

import androidx.room.TypeConverter
import java.time.Instant

object InstantTypeConverter {
    @TypeConverter
    fun fromInstant(instant: Instant): String = instant.toString()

    @TypeConverter
    fun fromString(instantString: String): Instant = Instant.parse(instantString)
}