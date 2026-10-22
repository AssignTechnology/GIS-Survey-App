package gis.data.survey.networkModule

import gis.data.survey.model.requestModel.BindPropertyTypeReq
import gis.data.survey.model.requestModel.SubPropertyTypeReq
import gis.data.survey.model.requestModel.ConstructionTypeReq
import gis.data.survey.model.requestModel.LoginReq
import gis.data.survey.model.requestModel.MohallaReq
import gis.data.survey.model.requestModel.PropertyReportReq
import gis.data.survey.model.requestModel.PropertyReq
import gis.data.survey.model.requestModel.PropertyKycReq
import gis.data.survey.model.requestModel.SendOtpReq
import gis.data.survey.model.requestModel.SequenceReq
import gis.data.survey.model.requestModel.VerifyOtpReq
import gis.data.survey.model.responseModel.BindPropertyTypeRes
import gis.data.survey.model.responseModel.ConstructionTypeRes
import gis.data.survey.model.responseModel.GovPropertyRes
import gis.data.survey.model.responseModel.FloorNoRes
import gis.data.survey.model.responseModel.LoginModel
import gis.data.survey.model.responseModel.MohallaRes
import gis.data.survey.model.responseModel.NonGovPropertyRes
import gis.data.survey.model.responseModel.PropertyReportModel
import gis.data.survey.model.responseModel.PropertyRes
import gis.data.survey.model.responseModel.PropertyKycRes
import gis.data.survey.model.responseModel.PropertyTypeRes
import gis.data.survey.model.responseModel.RoadTypeRes
import gis.data.survey.model.responseModel.SubPropertyTypeRes
import gis.data.survey.model.responseModel.SendOtpRes
import gis.data.survey.model.responseModel.SequenceRes
import gis.data.survey.model.responseModel.VerifyOtpRes
import gis.data.survey.model.responseModel.WardRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/User/PropertyType")
    suspend fun getPropertyType(): Response<PropertyTypeRes>

    @GET("api/User/GetFloorNo")
    suspend fun getFloorNo(): Response<FloorNoRes>

    @POST("api/User/BindPropertyType")
    suspend fun bindPropertyType(
        @Body request: BindPropertyTypeReq
    ): Response<BindPropertyTypeRes>

    @POST("api/User/SubPropertyType")
    suspend fun getSubPropertyType(
        @Body request: SubPropertyTypeReq
    ): Response<SubPropertyTypeRes>

    @POST("api/User/Login")
    suspend fun getLogin(
        @Body request: LoginReq
    ): Response<LoginModel>

    @POST("api/User/SendOtp")
    suspend fun sendOtp(
        @Body request: SendOtpReq
    ): Response<SendOtpRes>

    @POST("api/User/GetLogin")
    suspend fun verifyOtp(
        @Body request: VerifyOtpReq
    ): Response<VerifyOtpRes>

    @POST("api/User/PropertyDetails")
    suspend fun getPropertyReport(
        @Body request: PropertyReportReq
    ): Response<PropertyReportModel>

    @POST("api/User/BindWardDetail")
    suspend fun getWard(
        @Body request: Map<String, String> = emptyMap()
    ): Response<WardRes>

    @POST("api/User/GetMohallaDetailByWardId")
    suspend fun getMohalla(
        @Body request: MohallaReq
    ): Response<MohallaRes>

    @POST("api/User/GetSequence")
    suspend fun sequence(
        @Body request: SequenceReq
    ): Response<SequenceRes>

    @POST("api/User/PrivateProperty")
    suspend fun getNonGovProperty(
        @Body request: Map<String, String> = emptyMap()
    ): Response<NonGovPropertyRes>

    @POST("api/User/GovProperty")
    suspend fun getGovProperty(
        @Body request: Map<String, String> = emptyMap()
    ): Response<GovPropertyRes>

    @POST("api/User/BindConstructionType")
    suspend fun getConstructionType(
        @Body request: ConstructionTypeReq
    ): Response<ConstructionTypeRes>

    @GET("api/User/PrimaryConstractionType")
    suspend fun getPrimaryConstructionType(
    ): Response<ConstructionTypeRes>

    @GET("api/User/GetRoadType")
    suspend fun getRoadType(
    ): Response<RoadTypeRes>

    @POST("api/User/SurveyPropertyRegistration")
    suspend fun getPropertyRegistration(
        @Body propertyReq: PropertyReq
    ): Response<PropertyRes>

    @POST("api/User/Propertykyc")
    suspend fun propertyKyc(
        @Body request: PropertyKycReq
    ): Response<PropertyKycRes>
}
