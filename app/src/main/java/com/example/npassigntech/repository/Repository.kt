package com.example.npassigntech.repository

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
import com.example.npassigntech.networkModule.ApiService
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

    suspend fun getConstructionType(): Response<ConstructionTypeRes> {
        return apiService.getConstructionType()
    }

    suspend fun getPropertyRegistration(propertyReq: PropertyReq): Response<PropertyRes> {
        return apiService.getPropertyRegistration(propertyReq)
    }

}