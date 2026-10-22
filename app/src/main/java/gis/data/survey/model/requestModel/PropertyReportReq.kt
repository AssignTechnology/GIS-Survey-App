package gis.data.survey.model.requestModel

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PropertyReportReq(
    @SerializedName("UserId")
    val userId: String
)
