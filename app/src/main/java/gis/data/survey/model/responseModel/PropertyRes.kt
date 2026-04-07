package gis.data.survey.model.responseModel


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PropertyRes(
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
            @SerializedName("Id")
            val id: Int,
            @SerializedName("MohallaName")
            val mohallaName: String,
            @SerializedName("WardName")
            val wardName: String,
            @SerializedName("Sequence")
            val sequence: String,
            @SerializedName("Property")
            val `property`: String,
            @SerializedName("BhukhandPlotType")
            val bhukhandPlotType: String,
            @SerializedName("TotalArea")
            val totalArea: String,
            @SerializedName("EmptyArea")
            val emptyArea: String,
            @SerializedName("Residential")
            val residential: String,
            @SerializedName("Commercial")
            val commercial: String,
            @SerializedName("Nahar_Talab")
            val naharTalab: String,
            @SerializedName("PropertyType")
            val propertyType: String,
            @SerializedName("Gov_NonGov")
            val govNonGov: String,
            @SerializedName("GovProperty")
            val govProperty: String,
            @SerializedName("NonGovProperty")
            val nonGovProperty: String,
            @SerializedName("FloorNo")
            val floorNo: String,
            @SerializedName("ConstructionType")
            val constructionType: String,
            @SerializedName("ConstructionYear")
            val constructionYear: String,
            @SerializedName("PropertyNo")
            val propertyNo: String,
            @SerializedName("OwnerName")
            val ownerName: String,
            @SerializedName("FatherHusbandName")
            val fatherHusbandName: String,
            @SerializedName("Relation")
            val relation: String,
            @SerializedName("Gender")
            val gender: String,
            @SerializedName("Religious")
            val religious: String,
            @SerializedName("MobileNo")
            val mobileNo: String,
            @SerializedName("Address")
            val address: String,
            @SerializedName("RodeWide")
            val rodeWide: String,
            @SerializedName("ImagesUrl")
            val imagesUrl: String,
            @SerializedName("ULBCode")
            val ulbCode: String
        )
    }
}