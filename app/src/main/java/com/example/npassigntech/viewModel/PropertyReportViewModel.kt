package com.example.npassigntech.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.npassigntech.component.Result
import com.example.npassigntech.model.responseModel.PropertyReportModel
import com.example.npassigntech.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

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

    init {
        // Initial fetch: only all properties
        fetchAllProperties()
    }

    // Function to fetch all properties (userNo = "")
    private fun fetchAllProperties() {
        viewModelScope.launch {
            _allPropertyReport.value = Result.Loading
            try {
                val response = repository.getPropertyReport("") // Assuming empty userNo fetches all
                if (response.isSuccessful && response.body() != null) {
                    _allPropertyReport.value = Result.Success(response.body()!!)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _allPropertyReport.value = Result.Error(response.code(), errorMessage)
                }
            } catch (e: HttpException) {
                _allPropertyReport.value = Result.Error(e.code(), e.message())
            } catch (e: IOException) {
                _allPropertyReport.value = Result.Error(errorMessage = "Network error: ${e.message}")
            } catch (e: Exception) {
                _allPropertyReport.value = Result.Error(errorMessage = "Unexpected error: ${e.localizedMessage}")
            }
        }
    }

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

    fun refresh() {
        _isRefreshing.value = true
        viewModelScope.launch {
            fetchAllProperties()
            if (userNo.isNotEmpty()) {
                fetchUserProperties(userNo)
            }
            _isRefreshing.value = false
        }
    }
}