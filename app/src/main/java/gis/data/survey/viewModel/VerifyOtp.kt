package gis.data.survey.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gis.data.survey.model.responseModel.VerifyOtpRes
import gis.data.survey.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import gis.data.survey.component.Result

@HiltViewModel
class VerifyOtp @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _verifyOtp = MutableStateFlow<Result<VerifyOtpRes>>(
        Result.Idle)
    val verifyOtp: StateFlow<Result<VerifyOtpRes>> = _verifyOtp.asStateFlow()

    fun fetchVerifyOtp(mobileNo: String,otp: String) {
        viewModelScope.launch {
            _verifyOtp.value = Result.Loading
            try {
                val response = repository.verifyOtp(mobileNo,otp)
                _verifyOtp.value = if (response.isSuccessful && response.body() != null) {
                  Result.Success(response.body()!!)
                } else {
                    Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _verifyOtp.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

}