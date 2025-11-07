package com.example.npassigntech.networkModule

import com.example.npassigntech.model.requestModel.PropertyReq
import com.example.npassigntech.model.responseModel.ConstructionTypeRes
import com.example.npassigntech.model.responseModel.GovPropertyRes
import com.example.npassigntech.model.responseModel.LoginModel
import com.example.npassigntech.model.responseModel.MohallaRes
import com.example.npassigntech.model.responseModel.NonGovPropertyRes
import com.example.npassigntech.model.responseModel.PropertyReportModel
import com.example.npassigntech.model.responseModel.PropertyRes
import com.example.npassigntech.model.responseModel.SendOtpRes
import com.example.npassigntech.model.responseModel.SequenceRes
import com.example.npassigntech.model.responseModel.VerifyOtpRes
import com.example.npassigntech.model.responseModel.WardRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("api/User/Login")
    suspend fun getLogin(
        @Field("UserName") userName: String,
        @Field("Password") password: String
    ): Response<LoginModel>

    @FormUrlEncoded
    @POST("api/User/SendOtp")
    suspend fun sendOtp(
        @Field("MobileNo") mobileNo: String,
    ): Response<SendOtpRes>

    @FormUrlEncoded
    @POST("api/User/GetLogin")
    suspend fun verifyOtp(
        @Field("MobileNo") mobileNo: String,
        @Field("Otp") otp: String,
    ): Response<VerifyOtpRes>

    @FormUrlEncoded
    @POST("api/User/PropertyReport")
    suspend fun getPropertyReport(
        @Field("EntryUser") userNo: String
    ): Response<PropertyReportModel>


    @POST("api/User/BindWardDetail")
    suspend fun getWard(
    ): Response<WardRes>

    @FormUrlEncoded
    @POST("api/User/GetMohallaDetailByWardId")
    suspend fun getMohalla(
        @Field("Id") id: Int
    ): Response<MohallaRes>

    @FormUrlEncoded
    @POST("api/User/GetSequence")
    suspend fun sequence(
        @Field("WardNo") wardNo: String
    ): Response<SequenceRes>

    @POST("api/User/PrivateProperty")
    suspend fun getNonGovProperty(
    ): Response<NonGovPropertyRes>

    @POST("api/User/GovProperty")
    suspend fun getGovProperty(
    ): Response<GovPropertyRes>


    @FormUrlEncoded
    @POST("api/User/BindConstructionType")
    suspend fun getConstructionType(
        @Field("Id") id: String
    ): Response<ConstructionTypeRes>

    @POST("api/User/PrimaryConstructionType")
    suspend fun getPrimaryConstructionType(
    ): Response<ConstructionTypeRes>

    @POST("api/User/BindRoadWide")
    suspend fun getBindRoadWide(
    ): Response<ConstructionTypeRes>

    @POST("api/User/SurveyPropertyRegistration")
    suspend fun getPropertyRegistration(
        @Body propertyReq: PropertyReq
    ): Response<PropertyRes>
}