package com.example.npassigntech.viewModel

import androidx.lifecycle.ViewModel

import com.example.npassigntech.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

}
