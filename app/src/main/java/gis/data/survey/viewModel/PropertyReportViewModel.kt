package gis.data.survey.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gis.data.survey.model.responseModel.PropertyReportModel
import gis.data.survey.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import gis.data.survey.component.Result
import gis.data.survey.model.requestModel.PropertyKycReq

@HiltViewModel
class PropertyReportViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    // State for user-specific properties (filtered by userNo)
    private val _userPropertyReport = MutableStateFlow<Result<PropertyReportModel>>(Result.Idle)
    val userPropertyReport: StateFlow<Result<PropertyReportModel>> = _userPropertyReport.asStateFlow()

    // State for all properties (no filter, userNo = "")
    private val _allPropertyReport = MutableStateFlow<Result<PropertyReportModel>>(Result.Idle)
    val allPropertyReport: StateFlow<Result<PropertyReportModel>> = _allPropertyReport.asStateFlow()

    var userNo: String = ""

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Function to fetch user-specific properties
    fun fetchUserProperties(userNo: String) {
        viewModelScope.launch {
            _userPropertyReport.value = Result.Loading
            try {
                val response = repository.getPropertyReport(userNo)
                if (response.isSuccessful && response.body() != null) {
                    _userPropertyReport.value = Result.Success(response.body()!!)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _userPropertyReport.value = Result.Error(response.code(), errorMessage)
                }
            } catch (e: HttpException) {
                _userPropertyReport.value = Result.Error(e.code(), e.message())
            } catch (e: IOException) {
                _userPropertyReport.value = Result.Error(errorMessage = "Network error: ${e.message}")
            } catch (e: Exception) {
                _userPropertyReport.value = Result.Error(errorMessage = "Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    fun updateKyc(
        context: Context,
        uniqueId: String,
        aadharFront: String,
        aadharBack: String,
        panCard: String,
        regForm: String,
        propertyPaper: String,
        aiftidevit: String,
        image: String,
        onSuccess: () -> Unit
    ) {
        if (aadharFront.isBlank() && aadharBack.isBlank() && panCard.isBlank() && regForm.isBlank() && propertyPaper.isBlank() && aiftidevit.isBlank() && image.isBlank()) {
            Toast.makeText(context, "कम से कम एक तस्वीर अपलोड करना अनिवार्य है", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            try {
                val req = PropertyKycReq(
                    uniqueId = uniqueId,
                    aadharFront = aadharFront,
                    aadharBack = aadharBack,
                    panCard = panCard,
                    regForm = regForm,
                    propertyPaper = propertyPaper,
                    aiftidevit = aiftidevit,
                    image = image
                )
                val response = repository.propertyKyc(req)
                if (response.isSuccessful && response.body() != null) {
                    val res = response.body()!!
                    if (res.np.responseCode == "200") {
                        Toast.makeText(context, "KYC Updated Successfully", Toast.LENGTH_SHORT).show()
                        onSuccess()
                        if (userNo.isNotEmpty()) {
                            fetchUserProperties(userNo)
                        }
                    } else {
                        Toast.makeText(context, res.np.responseMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error updating KYC", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun refresh() {
        _isRefreshing.value = true
        viewModelScope.launch {
            if (userNo.isNotEmpty()) {
                fetchUserProperties(userNo)
            }
            _isRefreshing.value = false
        }
    }
}
