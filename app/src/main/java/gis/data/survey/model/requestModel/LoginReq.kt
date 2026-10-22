package gis.data.survey.model.requestModel

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class LoginReq(
    @SerializedName("UserName")
    val userName: String,
    @SerializedName("Password")
    val password: String
)
