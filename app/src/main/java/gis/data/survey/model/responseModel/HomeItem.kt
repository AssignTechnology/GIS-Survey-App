package gis.data.survey.model.responseModel

import androidx.compose.ui.graphics.vector.ImageVector
import com.google.gson.annotations.SerializedName
import gis.data.survey.navigation.Screen
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
