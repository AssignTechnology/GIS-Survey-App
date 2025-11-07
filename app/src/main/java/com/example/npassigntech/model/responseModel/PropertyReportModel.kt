package com.example.npassigntech.model.responseModel


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PropertyReportModel(
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
            @SerializedName("Address")
            val address: String,
            @SerializedName("BhukhandPlotType")
            val bhukhandPlotType: String,
            @SerializedName("Commercial")
            val commercial: String,
            @SerializedName("ConstructionType")
            val constructionType: String,
            @SerializedName("ConstructionYear")
            val constructionYear: String,
            @SerializedName("FatherHusbandName")
            val fatherHusbandName: String,
            @SerializedName("FloorNo")
            val floorNo: String,
            @SerializedName("Gender")
            val gender: String,
            @SerializedName("Gov_NonGov")
            val govNonGov: String,
            @SerializedName("GovProperty")
            val govProperty: String,
            @SerializedName("Id")
            val id: Int,
            @SerializedName("ImagesUrl")
            val imagesUrl: String,
            @SerializedName("MobileNo")
            val mobileNo: String,
            @SerializedName("MohallaName")
            val mohallaName: String,
            @SerializedName("Nahar_Talab")
            val naharTalab: String,
            @SerializedName("NonGovProperty")
            val nonGovProperty: String,
            @SerializedName("OwnerName")
            val ownerName: String,
            @SerializedName("Property")
            val `property`: String,
            @SerializedName("PropertyNo")
            val propertyNo: String,
            @SerializedName("PropertyType")
            val propertyType: String,
            @SerializedName("Relation")
            val relation: String,
            @SerializedName("Religious")
            val religious: String,
            @SerializedName("Residential")
            val residential: String,
            @SerializedName("RodeWide")
            val rodeWide: String,
            @SerializedName("Sequence")
            val sequence: String,
            @SerializedName("TotalArea")
            val totalArea: String,
            @SerializedName("WardName")
            val wardName: String,
            @SerializedName("EmptyArea")
            val emptyArea: String,
            @SerializedName("PrimaryConstructionType")
            val primaryConstructionType: String

        )
    }
}