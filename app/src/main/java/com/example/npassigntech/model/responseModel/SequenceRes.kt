package com.example.npassigntech.model.responseModel


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SequenceRes(
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
        val responseData: ResponseData
    ) {
        @Serializable
        data class ResponseData(
            @SerializedName("Sequence")
            val sequence: String
        )
    }
}