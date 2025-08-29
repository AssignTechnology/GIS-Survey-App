package com.example.npassigntech.model.responseModel


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ConstructionTypeRes(
    @SerializedName("NP")
    val nP: NP
) {
    @Serializable
    data class NP(
        @SerializedName("ResponseCode")
        val responseCode: String,
        @SerializedName("ResponseData")
        val responseData: List<ResponseData>,
        @SerializedName("ResponseMessage")
        val responseMessage: String
    ) {
        @Serializable
        data class ResponseData(
            @SerializedName("Id")
            val id: String,
            @SerializedName("Name")
            val name: String
        )
    }
}