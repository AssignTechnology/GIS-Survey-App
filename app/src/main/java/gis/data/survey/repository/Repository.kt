package gis.data.survey.repository

import gis.data.survey.model.requestModel.ConstructionTypeReq
import gis.data.survey.model.requestModel.LoginReq
import gis.data.survey.model.requestModel.MohallaReq
import gis.data.survey.model.requestModel.PropertyReportReq
import gis.data.survey.model.requestModel.PropertyReq
import gis.data.survey.model.requestModel.PropertyKycReq
import gis.data.survey.model.requestModel.SendOtpReq
import gis.data.survey.model.requestModel.SequenceReq
import gis.data.survey.model.requestModel.VerifyOtpReq
import gis.data.survey.model.requestModel.BindPropertyTypeReq
import gis.data.survey.model.requestModel.SubPropertyTypeReq
import gis.data.survey.model.responseModel.BindPropertyTypeRes
import gis.data.survey.model.responseModel.SubPropertyTypeRes
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
        return apiService.getLogin(LoginReq(admin, password))
    }

    suspend fun sendOtp(mobileNo: String): Response<SendOtpRes> {
        return apiService.sendOtp(SendOtpReq(mobileNo))
    }

    suspend fun verifyOtp(mobileNo: String, otp: String): Response<VerifyOtpRes> {
        return apiService.verifyOtp(VerifyOtpReq(mobileNo, otp))
    }

    suspend fun getPropertyType(): Response<PropertyTypeRes> {
        return apiService.getPropertyType()
    }

    suspend fun getFloorNo(): Response<FloorNoRes> {
        return apiService.getFloorNo()
    }

    suspend fun bindPropertyType(rcmPropertyId: Int): Response<BindPropertyTypeRes> {
        return apiService.bindPropertyType(BindPropertyTypeReq(rcmPropertyId))
    }

    suspend fun getSubPropertyType(id: String): Response<SubPropertyTypeRes> {
        return apiService.getSubPropertyType(SubPropertyTypeReq(id))
    }

    suspend fun getPropertyReport(userNo: String): Response<PropertyReportModel> {
        return apiService.getPropertyReport(PropertyReportReq(userNo))
    }

    suspend fun getWard(): Response<WardRes> {
        return apiService.getWard(emptyMap())
    }

    suspend fun getMohalla(id: Int): Response<MohallaRes> {
        return apiService.getMohalla(MohallaReq(id))
    }

    suspend fun getSequence(wardNo: String): Response<SequenceRes> {
        return apiService.sequence(SequenceReq(wardNo))
    }

    suspend fun getNonGovProperty(): Response<NonGovPropertyRes> {
        return apiService.getNonGovProperty(emptyMap())
    }

    suspend fun getGovProperty(): Response<GovPropertyRes> {
        return apiService.getGovProperty(emptyMap())
    }

    suspend fun getConstructionType(id: String): Response<ConstructionTypeRes> {
        return apiService.getConstructionType(ConstructionTypeReq(id))
    }
    

    
    suspend fun getPrimaryConstructionType(): Response<ConstructionTypeRes> {
        return apiService.getPrimaryConstructionType()
    }

    suspend fun getRoadType(): Response<RoadTypeRes> {
        return apiService.getRoadType()
    }

    suspend fun getPropertyRegistration(propertyReq: PropertyReq): Response<PropertyRes> {
        return apiService.getPropertyRegistration(propertyReq)
    }

    suspend fun propertyKyc(request: PropertyKycReq): Response<PropertyKycRes> {
        return apiService.propertyKyc(request)
    }
    
}
