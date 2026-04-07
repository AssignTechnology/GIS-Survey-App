package gis.data.survey.repository

import gis.data.survey.model.requestModel.PropertyReq
import gis.data.survey.model.responseModel.ConstructionTypeRes
import gis.data.survey.model.responseModel.GovPropertyRes
import gis.data.survey.model.responseModel.LoginModel
import gis.data.survey.model.responseModel.MohallaRes
import gis.data.survey.model.responseModel.NonGovPropertyRes
import gis.data.survey.model.responseModel.PropertyReportModel
import gis.data.survey.model.responseModel.PropertyRes
import gis.data.survey.model.responseModel.SendOtpRes
import gis.data.survey.model.responseModel.SequenceRes
import gis.data.survey.model.responseModel.VerifyOtpRes
import gis.data.survey.model.responseModel.WardRes
import gis.data.survey.networkModule.ApiService
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