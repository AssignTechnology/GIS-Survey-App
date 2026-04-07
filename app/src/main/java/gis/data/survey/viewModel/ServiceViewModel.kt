package gis.data.survey.viewModel

import androidx.lifecycle.ViewModel

import gis.data.survey.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

}
