package gis.data.survey.model.responseModel

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PropertyKycRes(
    @SerializedName("Np")
    val np: Np
) {
    @Serializable
    data class Np(
        @SerializedName("ResponseMessage")
        val responseMessage: String,
        @SerializedName("UniqueId")
        val uniqueId: String,
        @SerializedName("ResponseCode")
        val responseCode: String
    )
}
