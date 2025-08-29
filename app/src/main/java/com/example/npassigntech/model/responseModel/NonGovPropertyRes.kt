package com.example.npassigntech.model.responseModel


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class NonGovPropertyRes(
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
            @SerializedName("PrivateProperty")
            val privateProperty: String
        )
    }
}