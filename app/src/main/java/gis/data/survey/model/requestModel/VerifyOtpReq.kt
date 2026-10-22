package gis.data.survey.model.requestModel

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpReq(
    @SerializedName("MobileNo")
    val mobileNo: String,
    @SerializedName("Otp")
    val otp: String
)
