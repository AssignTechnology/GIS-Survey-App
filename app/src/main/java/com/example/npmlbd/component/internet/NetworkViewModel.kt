package com.example.npmlbd.component.internet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class NetworkViewModel(context: Context) : ViewModel() {
    val networkStatus: StateFlow<NetworkStatus> = context.observeNetworkStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keeps flow active for 5s after last collector
            initialValue = NetworkStatus.Disconnected
        )
}