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
