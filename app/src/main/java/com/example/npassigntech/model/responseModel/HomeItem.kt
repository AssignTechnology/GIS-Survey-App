package com.example.npassigntech.model.responseModel

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.npassigntech.navigation.Screen
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class HomeItem(
    @SerializedName("tittle")
    val title: String,
    @SerializedName("icon")
    val icon: ImageVector,
    @SerializedName("route")
    val route: Screen? = null
)
