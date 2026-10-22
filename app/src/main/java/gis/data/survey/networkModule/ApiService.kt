package gis.data.survey.networkModule




//import gis.data.survey.model.requestModel.BindPropertyTypeReq
//import gis.data.survey.model.requestModel.SubPropertyTypeReq
//import gis.data.survey.model.requestModel.ConstructionTypeReq
//import gis.data.survey.model.requestModel.LoginReq
//import gis.data.survey.model.requestModel.MohallaReq
//import gis.data.survey.model.requestModel.PropertyReportReq
//import gis.data.survey.model.requestModel.PropertyReq
//import gis.data.survey.model.requestModel.PropertyKycReq
//import gis.data.survey.model.requestModel.SendOtpReq
//import gis.data.survey.model.requestModel.SequenceReq
//import gis.data.survey.model.requestModel.VerifyOtpReq
//import gis.data.survey.model.responseModel.BindPropertyTypeRes
//import gis.data.survey.model.responseModel.ConstructionTypeRes
//import gis.data.survey.model.responseModel.GovPropertyRes
//import gis.data.survey.model.responseModel.FloorNoRes
//import gis.data.survey.model.responseModel.LoginModel
//import gis.data.survey.model.responseModel.MohallaRes
//import gis.data.survey.model.responseModel.NonGovPropertyRes
//import gis.data.survey.model.responseModel.PropertyReportModel
//import gis.data.survey.model.responseModel.PropertyRes
//import gis.data.survey.model.responseModel.PropertyKycRes
//import gis.data.survey.model.responseModel.PropertyTypeRes
//import gis.data.survey.model.responseModel.RoadTypeRes
//import gis.data.survey.model.responseModel.SubPropertyTypeRes
//import gis.data.survey.model.responseModel.SendOtpRes
//import gis.data.survey.model.responseModel.SequenceRes
//import gis.data.survey.model.responseModel.VerifyOtpRes
//import gis.data.survey.model.responseModel.WardRes
//import retrofit2.Response
//import retrofit2.http.Body
//import retrofit2.http.GET
//import retrofit2.http.POST
//
//interface ApiService {
//    @GET("api/User/PropertyType")
//    suspend fun getPropertyType(): Response<PropertyTypeRes>
//
//    @GET("api/User/GetFloorNo")
//    suspend fun getFloorNo(): Response<FloorNoRes>
//
//    @POST("api/User/BindPropertyType")
//    suspend fun bindPropertyType(
//        @Body request: BindPropertyTypeReq
//    ): Response<BindPropertyTypeRes>
//
//    @POST("api/User/SubPropertyType")
//    suspend fun getSubPropertyType(
//        @Body request: SubPropertyTypeReq
//    ): Response<SubPropertyTypeRes>
//
//    @POST("api/User/Login")
//    suspend fun getLogin(
//        @Body request: LoginReq
//    ): Response<LoginModel>
//
//    @POST("api/User/SendOtp")
//    suspend fun sendOtp(
//        @Body request: SendOtpReq
//    ): Response<SendOtpRes>
//
//    @POST("api/User/GetLogin")
//    suspend fun verifyOtp(
//        @Body request: VerifyOtpReq
//    ): Response<VerifyOtpRes>
//
//    @POST("api/User/PropertyDetails")
//    suspend fun getPropertyReport(
//        @Body request: PropertyReportReq
//    ): Response<PropertyReportModel>
//
//    @POST("api/User/BindWardDetail")
//    suspend fun getWard(
//        @Body request: Map<String, String> = emptyMap()
//    ): Response<WardRes>
//
//    @POST("api/User/GetMohallaDetailByWardId")
//    suspend fun getMohalla(
//        @Body request: MohallaReq
//    ): Response<MohallaRes>
//
//    @POST("api/User/GetSequence")
//    suspend fun sequence(
//        @Body request: SequenceReq
//    ): Response<SequenceRes>
//
//    @POST("api/User/PrivateProperty")
//    suspend fun getNonGovProperty(
//        @Body request: Map<String, String> = emptyMap()
//    ): Response<NonGovPropertyRes>
//
//    @POST("api/User/GovProperty")
//    suspend fun getGovProperty(
//        @Body request: Map<String, String> = emptyMap()
//    ): Response<GovPropertyRes>
//
//    @POST("api/User/BindConstructionType")
//    suspend fun getConstructionType(
//        @Body request: ConstructionTypeReq
//    ): Response<ConstructionTypeRes>
//
//    @GET("api/User/PrimaryConstractionType")
//    suspend fun getPrimaryConstructionType(
//    ): Response<ConstructionTypeRes>
//
//    @GET("api/User/GetRoadType")
//    suspend fun getRoadType(
//    ): Response<RoadTypeRes>
//
//    @POST("api/User/SurveyPropertyRegistration")
//    suspend fun getPropertyRegistration(
//        @Body propertyReq: PropertyReq
//    ): Response<PropertyRes>
//
//    @POST("api/User/Propertykyc")
//    suspend fun propertyKyc(
//        @Body request: PropertyKycReq
//    ): Response<PropertyKycRes>
//}


import gis.data.survey.model.requestModel.*
import gis.data.survey.model.responseModel.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.statement.*
import javax.inject.Inject
import javax.inject.Singleton

// Retrofit जैसा Response Wrapper ताकि ViewModels में कुछ न बदलना पड़े
data class Response<T>(
    val code: Int,
    val isSuccessful: Boolean,
    private val rawBody: T?,
    val errorBodyString: String?
) {
    fun body(): T? = rawBody
    fun errorBody(): ErrorBody? = errorBodyString?.let { ErrorBody(it) }

    class ErrorBody(private val errorStr: String) {
        fun string(): String = errorStr
    }
}

@Singleton
class ApiService @Inject constructor(
    private val client: HttpClient
) {
    private val baseUrl = "https://survey.assigntechnology.com/"

    private suspend inline fun <reified T> safePost(url: String, request: Any): Response<T> {
        return try {
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.isSuccess()) {
                val body: T = response.body()
                Response(response.status.value, true, body, null)
            } else {
                Response(response.status.value, false, null, response.bodyAsText())
            }
        } catch (e: Exception) {
            Response(500, false, null, e.message)
        }
    }

    private suspend inline fun <reified T> safeGet(url: String): Response<T> {
        return try {
            val response: HttpResponse = client.get(url)
            if (response.status.isSuccess()) {
                val body: T = response.body()
                Response(response.status.value, true, body, null)
            } else {
                Response(response.status.value, false, null, response.bodyAsText())
            }
        } catch (e: Exception) {
            Response(500, false, null, e.message)
        }
    }

    suspend fun getPropertyType(): Response<PropertyTypeRes> = safeGet("${baseUrl}api/User/PropertyType")

    suspend fun getFloorNo(): Response<FloorNoRes> = safeGet("${baseUrl}api/User/GetFloorNo")

    suspend fun getLogin(request: LoginReq): Response<LoginModel> = safePost("${baseUrl}api/User/Login", request)

    suspend fun sendOtp(request: SendOtpReq): Response<SendOtpRes> = safePost("${baseUrl}api/User/SendOtp", request)

    suspend fun verifyOtp(request: VerifyOtpReq): Response<VerifyOtpRes> = safePost("${baseUrl}api/User/GetLogin", request)

    suspend fun getPropertyReport(request: PropertyReportReq): Response<PropertyReportModel> = safePost("${baseUrl}api/User/PropertyDetails", request)

    suspend fun getWard(request: Map<String, String> = emptyMap()): Response<WardRes> = safePost("${baseUrl}api/User/BindWardDetail", request)

    suspend fun getMohalla(request: MohallaReq): Response<MohallaRes> = safePost("${baseUrl}api/User/GetMohallaDetailByWardId", request)

    suspend fun sequence(request: SequenceReq): Response<SequenceRes> = safePost("${baseUrl}api/User/GetSequence", request)

    suspend fun getNonGovProperty(request: Map<String, String> = emptyMap()): Response<NonGovPropertyRes> = safePost("${baseUrl}api/User/PrivateProperty", request)

    suspend fun getGovProperty(request: Map<String, String> = emptyMap()): Response<GovPropertyRes> = safePost("${baseUrl}api/User/GovProperty", request)

    suspend fun getConstructionType(request: ConstructionTypeReq): Response<ConstructionTypeRes> = safePost("${baseUrl}api/User/BindConstructionType", request)

    suspend fun getPrimaryConstructionType(): Response<ConstructionTypeRes> = safeGet("${baseUrl}api/User/PrimaryConstractionType")

    suspend fun getRoadType(): Response<RoadTypeRes> = safeGet("${baseUrl}api/User/GetRoadType")

    suspend fun getPropertyRegistration(propertyReq: PropertyReq): Response<PropertyRes> = safePost("${baseUrl}api/User/SurveyPropertyRegistration", propertyReq)

    suspend fun propertyKyc(request: PropertyKycReq): Response<PropertyKycRes> = safePost("${baseUrl}api/User/Propertykyc", request)

    suspend fun bindPropertyType(request: BindPropertyTypeReq): Response<BindPropertyTypeRes> = safePost("${baseUrl}api/User/BindPropertyType", request)

    suspend fun getSubPropertyType(request: SubPropertyTypeReq): Response<SubPropertyTypeRes> = safePost("${baseUrl}api/User/SubPropertyType", request)
}