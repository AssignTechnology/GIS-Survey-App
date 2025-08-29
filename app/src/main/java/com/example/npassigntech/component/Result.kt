package com.example.npassigntech.component

sealed class Result<out T> {
    object Idle : Result<Nothing>()
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val errorCode: Int? = null,
        val errorMessage: String
    ) : Result<Nothing>()
}