package com.onirutla.storyapp.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Patterns
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun String.isValidEmail(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun File.compressImage(): File {
    val bitmap = BitmapFactory.decodeFile(path)
    var streamLength: Int
    var compressQuality = 100
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))
    return this
}

@BindingAdapter("load_image")
fun loadImage(view: ImageView, any: Any?) {
    any?.let { Glide.with(view).load(it).into(view) }
}