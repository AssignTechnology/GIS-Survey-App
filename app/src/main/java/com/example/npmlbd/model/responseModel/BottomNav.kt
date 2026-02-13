package com.example.npmlbd.model.responseModel

import androidx.compose.ui.graphics.vector.ImageVector
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class BottomNav(
    @SerializedName("name")
    val name: String,
    @SerializedName("route")
    val route: String,
    @SerializedName("selectedIcon")
    val selectedIcon: ImageVector,
    @SerializedName("unselectedIcon")
    val unselectedIcon: ImageVector
)

