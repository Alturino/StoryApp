package com.onirutla.storyapp.core.domain

sealed interface ResponseState<out T> {
    data object Loading : ResponseState<Nothing>
    data object Initial : ResponseState<Nothing>
    data class Error(val message: String = "") : ResponseState<Nothing>
    data class Success<T>(val data: T) : ResponseState<T>

    fun onSuccess(f: Success<out T>.() -> Unit): ResponseState<T> {
        if (this is Success) {
            apply(f)
        }
        return this
    }

    fun onError(f: Error.() -> Unit): ResponseState<T> {
        if (this is Error) {
            apply(f)
        }
        return this
    }

    fun onLoading(f: Loading.() -> Unit): ResponseState<T> {
        if (this is Loading) {
            apply(f)
        }
        return this
    }

    fun onInitial(f: Initial.() -> Unit): ResponseState<T> {
        if (this is Initial) {
            apply(f)
        }
        return this
    }
}
