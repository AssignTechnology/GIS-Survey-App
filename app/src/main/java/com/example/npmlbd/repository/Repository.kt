package com.example.npmlbd.repository

import com.example.npmlbd.model.requestModel.PropertyReq
import com.example.npmlbd.model.responseModel.ConstructionTypeRes
import com.example.npmlbd.model.responseModel.GovPropertyRes
import com.example.npmlbd.model.responseModel.LoginModel
import com.example.npmlbd.model.responseModel.MohallaRes
import com.example.npmlbd.model.responseModel.NonGovPropertyRes
import com.example.npmlbd.model.responseModel.PropertyReportModel
import com.example.npmlbd.model.responseModel.PropertyRes
import com.example.npmlbd.model.responseModel.SendOtpRes
import com.example.npmlbd.model.responseModel.SequenceRes
import com.example.npmlbd.model.responseModel.VerifyOtpRes
import com.example.npmlbd.model.responseModel.WardRes
import com.example.npmlbd.networkModule.ApiService
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getLogin(admin: String, password: String): Response<LoginModel> {
        return apiService.getLogin(admin, password)
    }

    suspend fun sendOtp(mobileNo: String): Response<SendOtpRes> {
        return apiService.sendOtp(mobileNo)
    }

    suspend fun verifyOtp(mobileNo: String,otp: String): Response<VerifyOtpRes> {
        return apiService.verifyOtp(mobileNo,otp)
    }

    suspend fun getPropertyReport(userNo: String): Response<PropertyReportModel> {
        return apiService.getPropertyReport(userNo)
    }


    suspend fun getWard(): Response<WardRes> {
        return apiService.getWard()
    }

    suspend fun getMohalla(id: Int): Response<MohallaRes> {
        return apiService.getMohalla(id)
    }

    suspend fun getSequence(wardNo: String): Response<SequenceRes> {
        return apiService.sequence(wardNo)
    }

    suspend fun getNonGovProperty(): Response<NonGovPropertyRes> {
        return apiService.getNonGovProperty()
    }

    suspend fun getGovProperty(): Response<GovPropertyRes> {
        return apiService.getGovProperty()
    }

    suspend fun getConstructionType(id: String): Response<ConstructionTypeRes> {
        return apiService.getConstructionType(id)
    }

    suspend fun getPrimaryConstructionType(): Response<ConstructionTypeRes> {
        return apiService.getPrimaryConstructionType()
    }

    suspend fun getBindRoadWide(): Response<ConstructionTypeRes> {
        return apiService.getBindRoadWide()
    }

    suspend fun getPropertyRegistration(propertyReq: PropertyReq): Response<PropertyRes> {
        return apiService.getPropertyRegistration(propertyReq)
    }

}