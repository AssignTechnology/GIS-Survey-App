package gis.data.survey.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gis.data.survey.model.responseModel.SendOtpRes
import gis.data.survey.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import gis.data.survey.component.Result

@HiltViewModel
class OtpLoginViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _sendOtp = Channel<Result<SendOtpRes>>(Channel.BUFFERED)
    val sendOtp = _sendOtp.receiveAsFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchSendOtp(mobileNo: String) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.sendOtp(mobileNo)
                _sendOtp.send(
                    if (response.isSuccessful && response.body() != null) {
                        Result.Success(response.body()!!)
                    } else {
                        Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _sendOtp.send(Result.Error(errorMessage = e.message ?: "Unexpected error"))
            } finally {
                _isLoading.value = false
            }
        }
    }
}