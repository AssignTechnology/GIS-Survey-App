package gis.data.survey.model.requestModel


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PropertyReq(
    @SerializedName("WardNo")
    val wardNo: String,
    @SerializedName("MohallaName")
    val mohallaName: String,
    @SerializedName("OwnerName")
    val ownerName: String,
    @SerializedName("FatherHusbandName")
    val fatherHusbandName: String,
    @SerializedName("Relation")
    val relation: String,
    @SerializedName("Sequence")
    val sequence: String,
    @SerializedName("MobileNo")
    val mobileNo: String,
    @SerializedName("RodeWide")
    val rodeWide: String,
    @SerializedName("Property")
    val `property`: String,
    @SerializedName("PropertyType")
    val propertyType: String,
    @SerializedName("SubPropertyType")
    val subPropertyType: String,
    @SerializedName("GovNonGov")
    val govNonGov: String,
    @SerializedName("Residential")
    val residential: String,
    @SerializedName("Commercial")
    val commercial: String,
    @SerializedName("TotalArea")
    val totalArea: String,
    @SerializedName("FloorNo")
    val floorNo: String,
    @SerializedName("EmptyArea")
    val emptyArea: String,
    @SerializedName("ConstructionType")
    val constructionType: String,
    @SerializedName("Address")
    val address: String,
    @SerializedName("IpAddress")
    val ipAddress: String,
    @SerializedName("UserId")
    val userId: String,
    @SerializedName("Image")
    val image: String
)
