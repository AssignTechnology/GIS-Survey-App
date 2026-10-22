package gis.data.survey.model.responseModel

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
            @SerializedName("Id")
            val id: String = "",
            @SerializedName("WardNo")
            val wardNo: String = "",
            @SerializedName("WardName")
            val wardName: String = "",
            @SerializedName("MohallaName")
            val mohallaName: String = "",
            @SerializedName("Mohalla")
            val mohalla: String = "",
            @SerializedName("Sequence")
            val sequence: String = "",
            @SerializedName("Property")
            val property: String = "",
            @SerializedName("TotalArea")
            val totalArea: String = "",
            @SerializedName("EmptyArea")
            val emptyArea: String = "",
            @SerializedName("Residential")
            val residential: String = "",
            @SerializedName("Commercial")
            val commercial: String = "",
            @SerializedName("PropertyType")
            val propertyType: String = "",
            @SerializedName("GovProperty")
            val govProperty: String = "",
            @SerializedName("NonGovProperty")
            val nonGovProperty: String = "",
            @SerializedName("FloorNo")
            val floorNo: String = "",
            @SerializedName("ConstructionType")
            val constructionType: String = "",
            @SerializedName("PrimaryConstructionType")
            val primaryConstructionType: String = "",
            @SerializedName("ConstructionYear")
            val constructionYear: String = "",
            @SerializedName("PropertyNo")
            val propertyNo: String = "",
            @SerializedName("OwnerName")
            val ownerName: String = "",
            @SerializedName("FatherHusbandName")
            val fatherHusbandName: String = "",
            @SerializedName("Relation")
            val relation: String = "",
            @SerializedName("MobileNo")
            val mobileNo: String = "",
            @SerializedName("Address")
            val address: String = "",
            @SerializedName("RodeWide")
            val rodeWide: String = "",
            @SerializedName("ImageUrl")
            val imageUrl: String = "",
            @SerializedName("UniqueId")
            val uniqueId: String = "",
            @SerializedName("Formimg")
            val formimg: String = "",
            @SerializedName("Aadharimg")
            val aadharimg: String = "",
            @SerializedName("Aadharbackimg")
            val aadharbackimg: String = "",
            @SerializedName("Propertyimg")
            val propertyimg: String = "",
            @SerializedName("PanCard")
            val panCard: String = "",
            @SerializedName("Aiftidevitpath")
            val aiftidevitpath: String = "",
            @SerializedName("ImagesUrl")
            val imagesUrl: String = "",
            @SerializedName("BhukhandPlotType")
            val bhukhandPlotType: String = "",
            @SerializedName("Gender")
            val gender: String = "",
            @SerializedName("Gov_NonGov")
            val govNonGov: String = "",
            @SerializedName("Nahar_Talab")
            val naharTalab: String = "",
            @SerializedName("Religious")
            val religious: String = "",
            @SerializedName("ULBCode")
            val ulbCode: String = ""
        )
    }
}
