package com.example.npmlbd.component.internet

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Context.observeNetworkStatus(): Flow<NetworkStatus> = callbackFlow {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            val speed = capabilities?.linkDownstreamBandwidthKbps ?: 0
            if (speed > 1000) { // Example threshold: 1 Mbps for "good" connection
                trySend(NetworkStatus.Connected)
            } else {
                trySend(NetworkStatus.LowSpeed)
            }
        }

        override fun onLost(network: Network) {
            trySend(NetworkStatus.Disconnected)
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            val speed = networkCapabilities.linkDownstreamBandwidthKbps ?: 0
            if (speed > 1000) {
                trySend(NetworkStatus.Connected)
            } else {
                trySend(NetworkStatus.LowSpeed)
            }
        }
    }

    val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    connectivityManager.registerNetworkCallback(request, networkCallback)
    // Check initial state
    val activeNetwork = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
    if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
        val speed = capabilities.linkDownstreamBandwidthKbps ?: 0
        if (speed > 1000) {
            trySend(NetworkStatus.Connected)
        } else {
            trySend(NetworkStatus.LowSpeed)
        }
    } else {
        trySend(NetworkStatus.Disconnected)
    }

    awaitClose {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}