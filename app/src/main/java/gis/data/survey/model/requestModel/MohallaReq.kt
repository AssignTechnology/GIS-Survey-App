package gis.data.survey.model.requestModel

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MohallaReq(
    @SerializedName("Id")
    val id: Int
)
