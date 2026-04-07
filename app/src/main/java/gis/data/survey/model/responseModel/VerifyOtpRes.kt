package gis.data.survey.model.responseModel


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpRes(
    @SerializedName("NP")
    val nP: NP
) {
    @Serializable
    data class NP(
        @SerializedName("ResponseCode")
        val responseCode: String,
        @SerializedName("ResponseMessage")
        val responseMessage: String
    )
}