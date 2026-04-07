package gis.data.survey.model.responseModel


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class GovPropertyRes(
    @SerializedName("NP")
    val nP: NP
) {
    @Serializable
    data class NP(
        @SerializedName("ResponseMessage")
        val responseMessage: String,
        @SerializedName("ResponseCode")
        val responseCode: String,
        @SerializedName("ResponseData")
        val responseData: List<ResponseData>
    ) {
        @Serializable
        data class ResponseData(
            @SerializedName("Id")
            val id: Int,
            @SerializedName("GovProperty")
            val govProperty: String
        )
    }
}