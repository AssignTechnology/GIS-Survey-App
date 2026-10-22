package gis.data.survey.model.requestModel

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PropertyKycReq(
    @SerializedName("UniqueId")
    val uniqueId: String,
    @SerializedName("AadharFront")
    val aadharFront: String = "",
    @SerializedName("AadharBack")
    val aadharBack: String = "",
    @SerializedName("PanCard")
    val panCard: String = "",
    @SerializedName("RegForm")
    val regForm: String = "",
    @SerializedName("PropertyPaper")
    val propertyPaper: String = "",
    @SerializedName("Aiftidevit")
    val aiftidevit: String = "",
    @SerializedName("image")
    val image: String = ""
)
