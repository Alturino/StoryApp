package com.onirutla.storyapp.custom_component

import android.util.Patterns

fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()
