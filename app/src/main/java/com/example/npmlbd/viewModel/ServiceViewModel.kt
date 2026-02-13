package com.example.npmlbd.viewModel

import androidx.lifecycle.ViewModel

import com.example.npmlbd.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

}
