package com.example.npassigntech.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.npassigntech.component.Result
import com.example.npassigntech.model.responseModel.VerifyOtpRes
import com.example.npassigntech.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtp @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _verifyOtp = MutableStateFlow<com.example.npassigntech.component.Result<VerifyOtpRes>>(
        com.example.npassigntech.component.Result.Idle)
    val verifyOtp: StateFlow<com.example.npassigntech.component.Result<VerifyOtpRes>> = _verifyOtp.asStateFlow()

    fun fetchVerifyOtp(mobileNo: String,otp: String) {
        viewModelScope.launch {
            _verifyOtp.value = com.example.npassigntech.component.Result.Loading
            try {
                val response = repository.verifyOtp(mobileNo,otp)
                _verifyOtp.value = if (response.isSuccessful && response.body() != null) {
                    com.example.npassigntech.component.Result.Success(response.body()!!)
                } else {
                    com.example.npassigntech.component.Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _verifyOtp.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

}