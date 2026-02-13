package com.example.npmlbd.model.responseModel


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MohallaRes(
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
            @SerializedName("MohallaName")
            val mohallaName: String,
            @SerializedName("Id")
            val id: String
        )
    }
}