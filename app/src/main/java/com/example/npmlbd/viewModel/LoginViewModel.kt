package com.example.npmlbd.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.npmlbd.component.Result
import com.example.npmlbd.model.responseModel.LoginModel
import com.example.npmlbd.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Result<LoginModel>>(Result.Idle)
    val loginState: StateFlow<Result<LoginModel>> = _loginState.asStateFlow()

    var username by mutableStateOf("")
    var password by mutableStateOf("")



    fun login() {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            try {
                val response = repository.getLogin(username, password)
                if (response.isSuccessful && response.body() != null) {
                    _loginState.value = Result.Success(response.body()!!)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _loginState.value = Result.Error(response.code(), errorMessage)
                }
            } catch (e: HttpException) {
                _loginState.value = Result.Error(e.code(), e.message())
            } catch (e: IOException) {
                _loginState.value = Result.Error(errorMessage = "Network error: ${e.message}")
            } catch (e: Exception) {
                _loginState.value = Result.Error(errorMessage = "Unexpected error: ${e.localizedMessage}")
            }
        }
    }
}