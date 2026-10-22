package gis.data.survey.model.responseModel

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class BindPropertyTypeRes(
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
            @SerializedName("Property")
            val property: String = "",
            @SerializedName("Name")
            val name: String = "",
            @SerializedName("PropertyName")
            val propertyName: String = ""
        ) {
            val displayName: String
                get() = property.ifBlank { name.ifBlank { propertyName } }
        }
    }
}
