package gis.data.survey.model.responseModel


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PropertyRes(
    @SerializedName("Status")
    val status: String? = null,
    @SerializedName("ResponseMessage")
    val responseMessage: String,
    @SerializedName("ResponseCode")
    val responseCode: String,
    @SerializedName("UniqueId")
    val uniqueId: String
)
