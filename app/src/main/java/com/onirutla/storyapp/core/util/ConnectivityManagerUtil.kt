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

package com.onirutla.storyapp.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Context.isOnline(): Flow<Boolean> = callbackFlow {
    val connectivityManager = getSystemService(ConnectivityManager::class.java)
    val networkCallback = object : NetworkCallback() {
        override fun onUnavailable() {
            super.onUnavailable()
            trySend(false)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            trySend(false)
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            trySend(false)
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            trySend(true)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities,
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            trySend(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
        }
    }
    connectivityManager.registerDefaultNetworkCallback(networkCallback)
    awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
}
