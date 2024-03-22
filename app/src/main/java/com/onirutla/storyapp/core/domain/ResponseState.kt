/*
 * MIT License
 *
 * Copyright (c) 2023 Ricky Alturino
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
