package gis.data.survey.viewModel

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import gis.data.survey.component.ImagePicker
import gis.data.survey.model.requestModel.PropertyReq
import gis.data.survey.model.requestModel.PropertyKycReq
import gis.data.survey.model.responseModel.PropertyKycRes
import gis.data.survey.model.responseModel.BindPropertyTypeRes
import gis.data.survey.model.responseModel.ConstructionTypeRes
import gis.data.survey.model.responseModel.FloorNoRes
import gis.data.survey.model.responseModel.GovPropertyRes
import gis.data.survey.model.responseModel.MohallaRes
import gis.data.survey.model.responseModel.NonGovPropertyRes
import gis.data.survey.model.responseModel.PropertyTypeRes
import gis.data.survey.model.responseModel.RoadTypeRes
import gis.data.survey.model.responseModel.SubPropertyTypeRes
import gis.data.survey.model.responseModel.PropertyRes
import gis.data.survey.model.responseModel.SequenceRes
import gis.data.survey.model.responseModel.WardRes
import gis.data.survey.repository.Repository
import gis.data.survey.sharedPreferences.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import gis.data.survey.component.Result


@HiltViewModel
class PropertyRegistrationViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    // API response states
    private val _propertyRes = MutableStateFlow<Result<PropertyRes>>(Result.Idle)
    val propertyRes: StateFlow<Result<PropertyRes>> = _propertyRes.asStateFlow()

    private val _kycRes = MutableStateFlow<Result<PropertyKycRes>>(Result.Idle)
    val kycRes: StateFlow<Result<PropertyKycRes>> = _kycRes.asStateFlow()

    var registeredUniqueId by mutableStateOf("")
        private set
    private val _wardState = MutableStateFlow<Result<WardRes>>(Result.Idle)
    val wardState: StateFlow<Result<WardRes>> = _wardState.asStateFlow()

    private val _constructionType = MutableStateFlow<Result<ConstructionTypeRes>>(Result.Idle)
    val constructionType: StateFlow<Result<ConstructionTypeRes>> = _constructionType.asStateFlow()

    private val _pconstructionType = MutableStateFlow<Result<ConstructionTypeRes>>(Result.Idle)
    val pconstructionType: StateFlow<Result<ConstructionTypeRes>> = _pconstructionType.asStateFlow()

    private val _roadWide = MutableStateFlow<Result<RoadTypeRes>>(Result.Idle)
    val roadWideArea: StateFlow<Result<RoadTypeRes>> = _roadWide.asStateFlow()

    private val _nonGov = MutableStateFlow<Result<NonGovPropertyRes>>(Result.Idle)
    val nonGov: StateFlow<Result<NonGovPropertyRes>> = _nonGov.asStateFlow()

    private val _gov = MutableStateFlow<Result<GovPropertyRes>>(Result.Idle)
    val gov: StateFlow<Result<GovPropertyRes>> = _gov.asStateFlow()

    private val _propertyTypeState = MutableStateFlow<Result<PropertyTypeRes>>(Result.Idle)
    val propertyTypeState: StateFlow<Result<PropertyTypeRes>> = _propertyTypeState.asStateFlow()

    private val _floorState = MutableStateFlow<Result<FloorNoRes>>(Result.Idle)
    val floorState: StateFlow<Result<FloorNoRes>> = _floorState.asStateFlow()

    var selectedFloor = mutableStateOf<FloorNoRes.NP.ResponseData?>(null)

    fun updateFloor(item: FloorNoRes.NP.ResponseData?) {
        selectedFloor.value = item
        floorNo = item?.floorNo
        floorNoError = null
    }

    fun fetchFloorNos() {
        viewModelScope.launch {
            _floorState.value = Result.Loading
            try {
                val response = repository.getFloorNo()
                if (response.isSuccessful && response.body() != null) {
                    _floorState.value = Result.Success(response.body()!!)
                } else {
                    _floorState.value = Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _floorState.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    var selectedPropertyType = mutableStateOf<PropertyTypeRes.NP.ResponseData?>(null)

    fun fetchPropertyTypes() {
        viewModelScope.launch {
            _propertyTypeState.value = Result.Loading
            try {
                val response = repository.getPropertyType()
                if (response.isSuccessful && response.body() != null) {
                    android.util.Log.d("PropertyTypeAPI", "Response success: ${response.body()}")
                    _propertyTypeState.value = Result.Success(response.body()!!)
                } else {
                    val err = response.errorBody()?.string() ?: "Unknown error"
                    android.util.Log.e("PropertyTypeAPI", "Response error: $err")
                    _propertyTypeState.value = Result.Error(response.code(), err)
                }
            } catch (e: Exception) {
                android.util.Log.e("PropertyTypeAPI", "Exception: ${e.message}")
                _propertyTypeState.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    fun selectPropertyType(item: PropertyTypeRes.NP.ResponseData) {
        selectedPropertyType.value = item
        propertyType = item.propertyType
        propertyTypeError = null
        selectedBoundProperty.value = null
        selectedSubProperty.value = null
        subPropertyId = null

        // Fetch properties bound to this RCMPropertyId
        fetchBindPropertyType(item.rcmPropertyId)

        // Fetch sub-property types if TypeId is 1 (e.g. Bank & ATM)
        if (item.typeId == 1) {
            fetchSubPropertyTypes(item.id.toString())
        }

        // Clear gov/non-gov if GovPri is 0
        if (item.govPri == 0) {
            govNonGov = null
            govNonGovPos = null
            govId = null
            selectedGov.value = null
            nonGovId = null
            selectedNonGov.value = null
        }

        // Clear construction type if it violates CTypeId rule (0 = disable plot, 1 = only plot)
        val cTypeId = item.cTypeId
        val currentPConst = selectedpConstructionType.value
        if (currentPConst != null) {
            val isPlot = currentPConst.id == "1"
            if ((cTypeId == 0 && isPlot) || (cTypeId == 1 && !isPlot)) {
                selectedpConstructionType.value = null
                pconstructionTypeId = null
                updateConstructionTypeId("")
            }
        }

        val name = item.propertyType
        when {
            name.contains("भूखण्ड", ignoreCase = true) || name.contains("प्लाट", ignoreCase = true) || name.contains("भूमि", ignoreCase = true) -> {
                propertyTypePos = 2 // भूखंड/प्लाट
            }
            name.contains("मिश्रित", ignoreCase = true) || name.contains("+", ignoreCase = true) -> {
                propertyTypePos = 3 // मिश्रित
            }
            name.contains("निजी मकान", ignoreCase = true) || (name.contains("आवास", ignoreCase = true) && !name.contains("+", true) && !name.contains("दुकान", true) && !name.contains("क्लीनिक", true)) -> {
                propertyTypePos = 0 // आवासीय
            }
            else -> {
                propertyTypePos = 1 // व्यावसायिक
            }
        }
    }

    private val _bindPropertyTypeState = MutableStateFlow<Result<BindPropertyTypeRes>>(Result.Idle)
    val bindPropertyTypeState: StateFlow<Result<BindPropertyTypeRes>> = _bindPropertyTypeState.asStateFlow()

    var selectedBoundProperty = mutableStateOf<BindPropertyTypeRes.NP.ResponseData?>(null)

    private val _subPropertyState = MutableStateFlow<Result<SubPropertyTypeRes>>(Result.Idle)
    val subPropertyState: StateFlow<Result<SubPropertyTypeRes>> = _subPropertyState.asStateFlow()

    var selectedSubProperty = mutableStateOf<SubPropertyTypeRes.NP.ResponseData?>(null)
    var subPropertyId by mutableStateOf<String?>(null)
        private set

    fun updateSubProperty(item: SubPropertyTypeRes.NP.ResponseData?) {
        selectedSubProperty.value = item
        subPropertyId = item?.id?.toString()
    }

    fun fetchSubPropertyTypes(propertyTypeId: String) {
        viewModelScope.launch {
            _subPropertyState.value = Result.Loading
            try {
                val response = repository.getSubPropertyType(propertyTypeId)
                if (response.isSuccessful && response.body() != null) {
                    android.util.Log.d("SubPropertyAPI", "Response success: ${response.body()}")
                    _subPropertyState.value = Result.Success(response.body()!!)
                } else {
                    val err = response.errorBody()?.string() ?: "Unknown error"
                    android.util.Log.e("SubPropertyAPI", "Response error: $err")
                    _subPropertyState.value = Result.Error(response.code(), err)
                }
            } catch (e: Exception) {
                android.util.Log.e("SubPropertyAPI", "Exception: ${e.message}")
                _subPropertyState.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    fun fetchBindPropertyType(rcmPropertyId: Int) {
        viewModelScope.launch {
            _bindPropertyTypeState.value = Result.Loading
            try {
                val response = repository.bindPropertyType(rcmPropertyId)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    android.util.Log.d("BindPropertyTypeAPI", "Response success: $body")
                    _bindPropertyTypeState.value = Result.Success(body)

                    val propertyList = body.nP.responseData
                    onBindPropertyTypeLoaded(propertyList, selectedPropertyType.value)
                } else {
                    val err = response.errorBody()?.string() ?: "Unknown error"
                    android.util.Log.e("BindPropertyTypeAPI", "Response error: $err")
                    _bindPropertyTypeState.value = Result.Error(response.code(), err)
                }
            } catch (e: Exception) {
                android.util.Log.e("BindPropertyTypeAPI", "Exception: ${e.message}")
                _bindPropertyTypeState.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    private fun onBindPropertyTypeLoaded(propertyList: List<BindPropertyTypeRes.NP.ResponseData>, selectedType: PropertyTypeRes.NP.ResponseData?) {
        if (propertyList.isEmpty()) return

        val targetRcmId = selectedType?.rcmPropertyId ?: 0

        // Match directly by Id == rcmPropertyId first as requested by user
        val matched = propertyList.find { it.id == targetRcmId }
            ?: propertyList.find { item ->
                val pName = item.property
                val typeName = selectedType?.propertyType ?: ""
                when {
                    typeName.contains("दुकान", true) || typeName.contains("व्यावसायिक", true) || typeName.contains("होटल", true) || typeName.contains("रेस्टोरेंट", true) || typeName.contains("मॉल", true) || typeName.contains("कारखाना", true) -> pName.contains("व्यावसायिक", true)
                    typeName.contains("भूखण्ड", true) || typeName.contains("प्लाट", true) || typeName.contains("भूमि", true) -> pName.contains("भूखण्ड", true) || pName.contains("प्लाट", true)
                    typeName.contains("मिश्रित", true) || typeName.contains("+", true) -> pName.contains("मिश्रित", true)
                    typeName.contains("तालाब", true) || typeName.contains("कुआ", true) -> pName.contains("तालाब", true) || pName.contains("कुआ", true)
                    else -> pName.contains("आवासीय", true) || pName.contains("मकान", true)
                }
            }
            ?: propertyList[0]

        selectedBoundProperty.value = matched
    }

    private val _mohallaState = MutableStateFlow<Result<MohallaRes>>(Result.Idle)
    val mohallaState: StateFlow<Result<MohallaRes>> = _mohallaState.asStateFlow()

    private val _sequenceState = MutableStateFlow<Result<SequenceRes>>(Result.Idle)
    val sequenceState

            : StateFlow<Result<SequenceRes>> = _sequenceState.asStateFlow()

    // Selected IDs and data
    var wardId by mutableStateOf<String?>(null)
        private set

    fun updateWardId(value: String?) {
        wardId = value
        wardError = validateRequiredSelection(value, "वार्ड")
    }

    var selectedWard = mutableStateOf<WardRes.NP.ResponseData?>(null)

    var constructionTypeId by mutableStateOf<String?>(null)
        private set
    var pconstructionTypeId by mutableStateOf<String?>(null)
        private set

    fun updateConstructionTypeId(value: String?) {
        constructionTypeId = value
        constructionTypeError = validateRequiredSelection(value, "निर्माण के प्रकार")
    }

    fun updatepConstructionTypeId(value: String?) {
        pconstructionTypeId = value
        pconstructionTypeError = validateRequiredSelection(value, "निर्माण के प्रकार")
    }

    var selectedConstructionType = mutableStateOf<ConstructionTypeRes.NP.ResponseData?>(null)
    var selectedpConstructionType = mutableStateOf<ConstructionTypeRes.NP.ResponseData?>(null)
    var selectedRoadWide = mutableStateOf<RoadTypeRes.NP.ResponseData?>(null)

    var nonGovId by mutableStateOf<String?>(null)
        private set

    fun updateNonGovId(value: String?) {
        nonGovId = value
        nonGovError = validateRequiredSelection(value, "गैर सरकारी संम्पत्ति")
    }

    var selectedNonGov = mutableStateOf<NonGovPropertyRes.NP.ResponseData?>(null)

    fun selectNonGovProperty(item: NonGovPropertyRes.NP.ResponseData) {
        nonGovId = item.id.toString()
        selectedNonGov.value = item
        nonGovError = null

        val name = item.privateProperty
        when {
            name.contains("दुकान", ignoreCase = true) || name.contains("व्यावसायिक", ignoreCase = true) || name.contains("कॉमर्शियल", ignoreCase = true) || name.contains("होटल", ignoreCase = true) || name.contains("गोदाम", ignoreCase = true) -> {
                propertyType = "व्यावसायिक"
                propertyTypePos = 1
            }
            name.contains("भूखण्ड", ignoreCase = true) || name.contains("प्लाट", ignoreCase = true) || name.contains("भूमि", ignoreCase = true) -> {
                propertyType = "भूखंड/प्लाट"
                propertyTypePos = 2
            }
            name.contains("मिश्रित", ignoreCase = true) || name.contains("+", ignoreCase = true) -> {
                propertyType = "मिश्रित"
                propertyTypePos = 3
            }
            else -> {
                propertyType = "आवासीय"
                propertyTypePos = 0
            }
        }
    }

    var govId by mutableStateOf<String?>(null)
        private set

    fun updateGovId(value: String?) {
        govId = value
        govError = validateRequiredSelection(value, "सरकारी संम्पत्ति")
    }

    var selectedGov = mutableStateOf<GovPropertyRes.NP.ResponseData?>(null)

    fun selectGovProperty(item: GovPropertyRes.NP.ResponseData) {
        govId = item.id.toString()
        selectedGov.value = item
        govError = null

        val name = item.govProperty
        when {
            name.contains("दुकान", ignoreCase = true) || name.contains("व्यावसायिक", ignoreCase = true) -> {
                propertyType = "व्यावसायिक"
                propertyTypePos = 1
            }
            name.contains("भूखण्ड", ignoreCase = true) || name.contains("प्लाट", ignoreCase = true) -> {
                propertyType = "भूखंड/प्लाट"
                propertyTypePos = 2
            }
            else -> {
                propertyType = "आवासीय"
                propertyTypePos = 0
            }
        }
    }

    var mohallaId by mutableStateOf<String?>(null)
        private set

    fun updateMohallaId(value: String?) {
        mohallaId = value
        mohallaError = validateRequiredSelection(value, "मोहल्ला")
    }

    var selectedMohalla = mutableStateOf<MohallaRes.NP.ResponseData?>(null)

    var sequence by mutableStateOf<String?>(null)
        private set

    fun updateSequence(value: String?) {
        sequence = value
    }

    // Form fields
    var sequenceNo by mutableStateOf(TextFieldValue())
    var propertyNo by mutableStateOf(TextFieldValue())
    var ownerPropertyName by mutableStateOf(TextFieldValue())
    var fatherHusbandOwner by mutableStateOf(TextFieldValue())
    var contactNumber by mutableStateOf(TextFieldValue())
    var address by mutableStateOf(TextFieldValue())
    var residential by mutableStateOf(TextFieldValue())
    var commercial by mutableStateOf(TextFieldValue())
    var emptyArea by mutableStateOf(TextFieldValue())
    var totalArea by mutableStateOf(TextFieldValue())
    var propertyImage by mutableStateOf<ImagePicker.ImageResult?>(null)

    // Selections
    var property by mutableStateOf<String?>(null)
    var propertyPos by mutableStateOf<Int?>(null)
    fun updateProperty(value: String?, pos: Int?) {
        property = value
        propertyPos = pos
        propertyError = validateRequiredSelection(value, "संम्पत्ति")
        // Reset dependent fields based on property type
        when (pos) {
            0 -> { // भूखण्ड/प्लाट
                resetPropertyFields()
                bhukhandPlat = null
                bhukhandPlatError = null
            }

            1 -> { // तालाब/पोखरा/etc
                resetPropertyFields()
                talabPokharaEtc = null
                talabPokharaEtcError = null
            }

            2 -> { // संम्पत्ति
                resetPropertyFields()
                propertyType = null
                propertyTypePos = null
                propertyTypeError = null
                govNonGov = null
                govNonGovPos = null
                govNonGovError = null
            }

            else -> resetPropertyFields()
        }
    }

    var bhukhandPlat by mutableStateOf<String?>(null)
    var talabPokharaEtc by mutableStateOf<String?>(null)
    var propertyType by mutableStateOf<String?>(null)
    var propertyTypePos by mutableStateOf<Int?>(null)
    var govNonGov by mutableStateOf<String?>(null)
    var govNonGovPos by mutableStateOf<Int?>(null)
    var relation by mutableStateOf<String?>(null)
    var gender by mutableStateOf<String?>(null)
    var religion by mutableStateOf<String?>(null)
    var roadWide by mutableStateOf<String?>(null)
    var floorNo by mutableStateOf<String?>(null)

    // Error states
    var sequenceNoError by mutableStateOf<String?>(null)
    var propertyNoError by mutableStateOf<String?>(null)
    var ownerPropertyNameError by mutableStateOf<String?>(null)
    var fatherHusbandOwnerError by mutableStateOf<String?>(null)
    var contactNumberError by mutableStateOf<String?>(null)
    var addressError by mutableStateOf<String?>(null)
    var residentialError by mutableStateOf<String?>(null)
    var commercialError by mutableStateOf<String?>(null)
    var emptyAreaError by mutableStateOf<String?>(null)
    var totalAreaError by mutableStateOf<String?>(null)
    var propertyImageError by mutableStateOf<String?>(null)
    var propertyError by mutableStateOf<String?>(null)
    var bhukhandPlatError by mutableStateOf<String?>(null)
    var talabPokharaEtcError by mutableStateOf<String?>(null)
    var propertyTypeError by mutableStateOf<String?>(null)
    var govNonGovError by mutableStateOf<String?>(null)
    var relationError by mutableStateOf<String?>(null)
    var genderError by mutableStateOf<String?>(null)
    var religionError by mutableStateOf<String?>(null)
    var roadWideError by mutableStateOf<String?>(null)
    var floorNoError by mutableStateOf<String?>(null)
    var wardError by mutableStateOf<String?>(null)
    var mohallaError by mutableStateOf<String?>(null)
    var govError by mutableStateOf<String?>(null)
    var nonGovError by mutableStateOf<String?>(null)
    var constructionTypeError by mutableStateOf<String?>(null)
    var pconstructionTypeError by mutableStateOf<String?>(null)


    // In PropertyRegistrationViewModel

    fun updateResidential(value: TextFieldValue) {
        residential = value
        residentialError = validateArea(value.text, "आवासीय क्षेत्रफल")
        updateTotalAreaIfValid()
    }

    fun updateCommercial(value: TextFieldValue) {
        commercial = value
        commercialError = validateArea(value.text, "व्यावसायिक क्षेत्रफल")
        updateTotalAreaIfValid()
    }

    fun updateEmptyArea(value: TextFieldValue) {
        emptyArea = value
        emptyAreaError = validateArea(value.text, "खाली क्षेत्रफल")
        updateTotalAreaIfValid()
    }

    // Function to calculate total area as sum of residential, commercial, and empty area
    private fun updateTotalAreaIfValid() {
        // Only calculate if all fields are valid integers
        val residentialValue = residential.text.toIntOrNull() ?: 0
        val commercialValue = commercial.text.toIntOrNull() ?: 0
        val emptyAreaValue = emptyArea.text.toIntOrNull() ?: 0

        // Check if all fields are valid (no errors)
        if (residentialError == null && commercialError == null && emptyAreaError == null) {
            val total = residentialValue + commercialValue + emptyAreaValue
            totalArea = TextFieldValue(total.toString())
            totalAreaError = null
        } else {
            // If any field is invalid, re-validate totalArea
            totalAreaError = validateArea(totalArea.text, "सम्पूर्ण क्षेत्रफल")
        }
    }

    // Validation function for area fields
//    private fun validateArea(value: String, fieldName: String): String? {
//        return when {
//            value.isBlank() -> "$fieldName आवश्यक है"
//            !value.matches(Regex("^[0-9]*\\.?[0-9]*$")) -> "$fieldName में केवल सकारात्मक संख्या होनी चाहिए"
//            else -> null
//        }
//    }

    // Update functions with validation
    fun updateSequenceNo(value: TextFieldValue) {
        sequenceNo = value
        sequenceNoError = validateSequenceNo(value.text)
    }

    fun updatePropertyNo(value: TextFieldValue) {
        propertyNo = value
        propertyNoError = validatePropertyNo(value.text)
    }

    fun updateOwnerPropertyName(value: TextFieldValue) {
        ownerPropertyName = value
        ownerPropertyNameError = validateName(value.text)
    }

    fun updateFatherHusbandOwner(value: TextFieldValue) {
        fatherHusbandOwner = value
        fatherHusbandOwnerError = validateName(value.text)
    }

    fun updateContactNumber(value: TextFieldValue) {
        contactNumber = value
        contactNumberError = validateContactNumber(value.text)
    }

    fun updateAddress(value: TextFieldValue) {
        address = value
        addressError = validateAddress(value.text)
    }

//    fun updateResidential(value: TextFieldValue) {
//        residential = value
//        residentialError = validateArea(value.text, "आवासीय क्षेत्रफल")
//    }
//
//    fun updateCommercial(value: TextFieldValue) {
//        commercial = value
//        commercialError = validateArea(value.text, "व्यावसायिक क्षेत्रफल")
//    }
//
//    fun updateEmptyArea(value: TextFieldValue) {
//        emptyArea = value
//        emptyAreaError = validateArea(value.text, "खाली क्षेत्रफल")
//    }

    fun updateTotalArea(value: TextFieldValue) {
        totalArea = value
        totalAreaError = validateArea(value.text, "सम्पूर्ण क्षेत्रफल")
    }



    fun updatePropertyImage(image: ImagePicker.ImageResult?) {
        propertyImage = image
        propertyImageError = validatePropertyImage(image)
    }

    fun updateBhukhandPlat(value: String?) {
        bhukhandPlat = value
        bhukhandPlatError = validateRequiredSelection(value, "भूखण्ड/प्लाट")
    }

    fun updateTalabPokharaEtc(value: String?) {
        talabPokharaEtc = value
        talabPokharaEtcError = validateRequiredSelection(value, "तालाब/पोखरा/कुआँ/नहर/इत्यादि")
    }

    fun updatePropertyType(value: String?, pos: Int?) {
        propertyType = value
        propertyTypePos = pos
        propertyTypeError = validateRequiredSelection(value, "संम्पत्ति के प्रकार")
        // Reset dependent fields based on property type
        when (pos) {
            0, 1, 2 -> { // आवासीय, व्यवसायिक, मिश्रित
                residential = TextFieldValue()
                residentialError = null
                commercial = TextFieldValue()
                commercialError = null
                emptyArea = TextFieldValue()
                emptyAreaError = null
                totalArea = TextFieldValue()
                totalAreaError = null
            }
        }
    }

    fun updateGovNonGov(value: String?, pos: Int?) {
        govNonGov = value
        govNonGovPos = pos


        govNonGovError = validateRequiredSelection(value, "सरकारी/गैर सरकारी")
        // Reset dependent fields
        when (pos) {
            0 -> { // सरकारी
                nonGovId = null
                nonGovError = null
            }

            1 -> { // गैर सरकारी
                govId = null
                govError = null
            }
        }
    }

    fun updateRelation(value: String?) {
        relation = value
        relationError = validateRequiredSelection(value, "सम्बंध")
    }

    fun updateGender(value: String?) {
        gender = value
        genderError = validateRequiredSelection(value, "लिंग")
    }

    fun updateReligion(value: String?) {
        religion = value
        religionError = validateRequiredSelection(value, "धर्म")
    }

    fun updateRoadWide(value: String?) {
        roadWide = value
        roadWideError = validateRequiredSelection(value, "रोड की चौड़ाई")
    }



    fun updateFloorNo(value: String?) {
        floorNo = value
        floorNoError = validateRequiredSelection(value, "फ्लोर नं")
    }

    // Validation functions
    private fun validateSequenceNo(value: String): String? {
        return when {
            value.isBlank() -> "क्रम संख्या आवश्यक है"
            !value.matches(Regex("^[0-9]+$")) -> "क्रम संख्या केवल अंक होनी चाहिए"
            else -> null
        }
    }

    private fun validatePropertyNo(value: String): String? {
        return when {
            value.isBlank() -> "संम्पत्ति संख्या आवश्यक है"
//            !value.matches(Regex("^[A-Za-z0-9]+$")) -> "संम्पत्ति संख्या में केवल अक्षर और अंक हो सकते हैं"
            else -> null
        }
    }

//    private fun validateName(value: String): String? {
//        return when {
//            value.isBlank() -> "नाम आवश्यक है"
//            !value.matches(Regex("^[\\u0900-\\u097F\\s]+$")) -> "नाम में केवल हिंदी अक्षर और रिक्त स्थान हो सकते हैं"
//            else -> null
//        }
//    }

    private fun validateName(value: String): String? {
        return when {
            value.isBlank() -> "नाम आवश्यक है"
            // Updated Regex: Added \u0900-\u097F, \s, (), and .
            !value.matches(Regex("^[\\u0900-\\u097F\\s().]+$")) ->
                "नाम में केवल हिंदी अक्षर, रिक्त स्थान, () और . हो सकते हैं"
            else -> null
        }
    }

    private fun validateContactNumber(value: String): String? {
        return when {
            value.isBlank() -> "मोबाइल नंबर आवश्यक है"
            !value.matches(Regex("^[0-9]{10}$")) -> "मोबाइल नंबर 10 अंकों का होना चाहिए"
            else -> null
        }
    }

    private fun validateAddress(value: String): String? {
        return when {
            value.isBlank() -> "पता आवश्यक है"
            value.length < 5 -> "पता कम से कम 5 अक्षरों का होना चाहिए"
            else -> null
        }
    }

    private fun validateArea(value: String, fieldName: String): String? {
        return when {
            value.isBlank() -> "$fieldName आवश्यक है"
            !value.matches(Regex("^[0-9]*\\.?[0-9]+$")) -> "$fieldName में केवल सकारात्मक संख्या होनी चाहिए"
            else -> null
        }
    }



    private fun validatePropertyImage(image: ImagePicker.ImageResult?): String? {
        return if (image == null || image.uri == null || image.base64.isNullOrBlank()) {
            "संपत्ति की फोटो अपलोड करना आवश्यक है"
        } else {
            null
        }
    }

    private fun validateRequiredSelection(value: Any?, fieldName: String): String? {
        return if (value == null) "$fieldName का चयन आवश्यक है" else null
    }

    // Helper function to reset property-related fields
    private fun resetPropertyFields() {
        bhukhandPlat = null
        bhukhandPlatError = null
        talabPokharaEtc = null
        talabPokharaEtcError = null
        propertyType = null
        propertyTypePos = null
        propertyTypeError = null
        govNonGov = null
        govNonGovPos = null
        govNonGovError = null
        govId = null
        govError = null
        nonGovId = null
        nonGovError = null
        floorNo = null
        floorNoError = null
        constructionTypeId = null
        constructionTypeError = null

        residential = TextFieldValue()
        residentialError = null
        commercial = TextFieldValue()
        commercialError = null
        emptyArea = TextFieldValue()
        emptyAreaError = null
        totalArea = TextFieldValue()
        totalAreaError = null
    }

    // API calls
    fun fetchWards() {
        viewModelScope.launch {
            _wardState.value = Result.Loading
            try {
                val response = repository.getWard()
                _wardState.value = if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _wardState.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    fun fetchMohalla(id: Int) {
        viewModelScope.launch {
            _mohallaState.value = Result.Loading
            try {
                val response = repository.getMohalla(id)
                _mohallaState.value = if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _mohallaState.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    fun fetchSequence(wardNo: String) {
        viewModelScope.launch {
            _sequenceState.value = Result.Loading
            try {
                val response = repository.getSequence(wardNo)
                _sequenceState.value = if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _sequenceState.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    fun fetchGovProperties() {
        viewModelScope.launch {
            _gov.value = Result.Loading
            try {
                val response = repository.getGovProperty()
                _gov.value = if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _gov.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    fun fetchNonGovProperties() {
        viewModelScope.launch {
            _nonGov.value = Result.Loading
            try {
                val response = repository.getNonGovProperty()
                _nonGov.value = if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _nonGov.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    fun fetchConstructionTypes(id: String) {
        viewModelScope.launch {
            _constructionType.value = Result.Loading
            try {
                val response = repository.getConstructionType(id)
                _constructionType.value = if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _constructionType.value =
                    Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    fun fetchPConstructionTypes() {
        viewModelScope.launch {
            _pconstructionType.value = Result.Loading
            try {
                val response = repository.getPrimaryConstructionType()
                _pconstructionType.value = if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _pconstructionType.value =
                    Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    fun fetchRoadWide() {
        viewModelScope.launch {
            _roadWide.value = Result.Loading
            try {
                val response = repository.getRoadType()
                _roadWide.value = if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _roadWide.value =
                    Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
    }

    // Validate form (without image)
    fun validateForm(context: Context): Boolean {
        var isValid = true
        sequenceNoError = validateSequenceNo(sequenceNo.text); if (sequenceNoError != null) isValid = false
        propertyNoError = validatePropertyNo(propertyNo.text); if (propertyNoError != null) isValid = false
        ownerPropertyNameError = validateName(ownerPropertyName.text); if (ownerPropertyNameError != null) isValid = false
        fatherHusbandOwnerError = validateName(fatherHusbandOwner.text); if (fatherHusbandOwnerError != null) isValid = false
        contactNumberError = validateContactNumber(contactNumber.text); if (contactNumberError != null) isValid = false
        addressError = validateAddress(address.text); if (addressError != null) isValid = false
        relationError = validateRequiredSelection(relation, "सम्बंध"); if (relationError != null) isValid = false
        roadWideError = validateRequiredSelection(roadWide, "रोड की चौड़ाई"); if (roadWideError != null) isValid = false
        wardError = validateRequiredSelection(wardId, "वार्ड"); if (wardError != null) isValid = false
        mohallaError = validateRequiredSelection(mohallaId, "मोहल्ला"); if (mohallaError != null) isValid = false
        return isValid
    }

    fun validateImage(): Boolean {
        propertyImageError = validatePropertyImage(propertyImage)
        return propertyImageError == null
    }

    // Submit form
    fun submitForm(context: Context, isPreview: Boolean = false): Boolean {
        var isValid = validateForm(context)

        // Validate property-specific fields
        when (propertyPos) {
            0 -> { // भूखण्ड/प्लाट
                bhukhandPlatError = validateRequiredSelection(
                    bhukhandPlat,
                    "भूखण्ड/प्लाट"
                );
//                if (bhukhandPlatError != null) isValid = false
//                residentialError = validateArea(
//                    residential.text,
//                    "आवासीय क्षेत्रफल"
//                ); if (residentialError != null) isValid = false
//                commercialError = validateArea(
//                    commercial.text,
//                    "व्यावसायिक क्षेत्रफल"
//                ); if (commercialError != null) isValid = false
                emptyAreaError = validateArea(
                    emptyArea.text,
                    "खाली क्षेत्रफल"
                ); if (emptyAreaError != null) isValid = false
                totalAreaError = validateArea(
                    totalArea.text,
                    "सम्पूर्ण क्षेत्रफल"
                ); if (totalAreaError != null) isValid = false
            }

            1 -> { // तालाब/पोखरा/etc
                talabPokharaEtcError = validateRequiredSelection(
                    talabPokharaEtc,
                    "तालाब/पोखरा/कुआँ/नहर/इत्यादि"
                ); if (talabPokharaEtcError != null) isValid = false
            }

            2 -> { // संम्पत्ति
                propertyTypeError = validateRequiredSelection(
                    propertyType,
                    "संम्पत्ति के प्रकार"
                ); if (propertyTypeError != null) isValid = false
                govNonGovError = validateRequiredSelection(
                    govNonGov,
                    "सरकारी/गैर सरकारी"
                ); if (govNonGovError != null) isValid = false

                when (propertyTypePos) {
                    0 -> { // आवासीय
                        residentialError = validateArea(
                            residential.text,
                            "आवासीय क्षेत्रफल"
                        ); if (residentialError != null) isValid = false
                        emptyAreaError = validateArea(
                            emptyArea.text,
                            "खाली क्षेत्रफल"
                        ); if (emptyAreaError != null) isValid = false
                        totalAreaError = validateArea(
                            totalArea.text,
                            "सम्पूर्ण क्षेत्रफल"
                        ); if (totalAreaError != null) isValid = false
                    }

                    1 -> { // व्यवसायिक
                        commercialError = validateArea(
                            commercial.text,
                            "व्यावसायिक क्षेत्रफल"
                        ); if (commercialError != null) isValid = false
                        emptyAreaError = validateArea(
                            emptyArea.text,
                            "खाली क्षेत्रफल"
                        ); if (emptyAreaError != null) isValid = false
                        totalAreaError = validateArea(
                            totalArea.text,
                            "सम्पूर्ण क्षेत्रफल"
                        ); if (totalAreaError != null) isValid = false
                    }

                    2 -> { // मिश्रित
                        residentialError = validateArea(
                            residential.text,
                            "आवासीय क्षेत्रफल"
                        ); if (residentialError != null) isValid = false
                        commercialError = validateArea(
                            commercial.text,
                            "व्यावसायिक क्षेत्रफल"
                        ); if (commercialError != null) isValid = false
                        emptyAreaError = validateArea(
                            emptyArea.text,
                            "खाली क्षेत्रफल"
                        ); if (emptyAreaError != null) isValid = false
                        totalAreaError = validateArea(
                            totalArea.text,
                            "सम्पूर्ण क्षेत्रफल"
                        ); if (totalAreaError != null) isValid = false
                    }
                }

                when (govNonGovPos) {
                    0 -> { // सरकारी
                        govError = validateRequiredSelection(
                            govId,
                            "सरकारी संम्पत्ति"
                        ); if (govError != null) isValid = false
                    }

                    1 -> { // गैर सरकारी
                        nonGovError = validateRequiredSelection(
                            nonGovId,
                            "गैर सरकारी संम्पत्ति"
                        ); if (nonGovError != null) isValid = false
                    }
                }

                floorNoError = validateRequiredSelection(
                    floorNo,
                    "फ्लोर नं"
                );


                if (floorNoError != null) isValid = false


                pconstructionTypeError = validateRequiredSelection(
                    pconstructionTypeId,
                    "निर्माण के प्रकार"
                );
                if (pconstructionTypeError != null) isValid = false
                constructionTypeError = validateRequiredSelection(
                    constructionTypeId,
                    "उपनिर्माण के प्रकार"
                );
                if (constructionTypeError != null) isValid = false

            }
        }

        if (!isValid) {
            val failedFields = mutableListOf<String>()
            if (sequenceNoError != null) failedFields.add("क्रम संख्या")
            if (propertyNoError != null) failedFields.add("संम्पत्ति संख्या")
            if (ownerPropertyNameError != null) failedFields.add("मुखिया/नाम")
            if (fatherHusbandOwnerError != null) failedFields.add("पिता/पति नाम")
            if (contactNumberError != null) failedFields.add("मोबाइल नंबर")
            if (addressError != null) failedFields.add("पता")
            if (relationError != null) failedFields.add("सम्बंध")
            if (roadWideError != null) failedFields.add("रोड की चौड़ाई")
            if (wardError != null) failedFields.add("वार्ड")
            if (mohallaError != null) failedFields.add("मोहल्ला")

            val msg = "त्रुटि: ${failedFields.joinToString(", ")}"
            android.util.Log.e("ValidationFailed", msg)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            return false
        }

        // Skip API call if in preview mode
        if (isPreview) {
            return true
        }
        val userIdVal = SharedPreferencesManager.getInstance(context).getValue("UserId", "").toString()
            .ifBlank { SharedPreferencesManager.getInstance(context).getValue("UserNumber", "App").toString() }
        val ipAddressVal = getDeviceIpAddress()

        val propertyReq = PropertyReq(
            wardNo = wardId ?: "",
            mohallaName = mohallaId ?: "",
            ownerName = ownerPropertyName.text,
            fatherHusbandName = fatherHusbandOwner.text,
            relation = relation ?: "",
            sequence = sequence?.let { it + sequenceNo.text } ?: sequenceNo.text,
            mobileNo = contactNumber.text,
            rodeWide = selectedRoadWide.value?.id?.toString() ?: roadWide ?: "",
            property = selectedBoundProperty.value?.id?.toString() ?: propertyPos?.toString() ?: "",
            propertyType = selectedPropertyType.value?.id?.toString() ?: "",
            subPropertyType = subPropertyId ?: selectedSubProperty.value?.id?.toString() ?: "",
            govNonGov = govNonGov ?: "",
            residential = residential.text,
            commercial = commercial.text,
            totalArea = totalArea.text,
            floorNo = selectedFloor.value?.id?.toString() ?: floorNo ?: "",
            emptyArea = emptyArea.text,
            constructionType = constructionTypeId?.takeIf { !it.isBlank() } ?: pconstructionTypeId ?: "",
            address = address.text,
            ipAddress = ipAddressVal,
            userId = userIdVal,
            image = propertyImage?.base64 ?: ""
        )

        viewModelScope.launch {
            _propertyRes.value = Result.Loading
            try {
                val response = repository.getPropertyRegistration(propertyReq)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    registeredUniqueId = body.uniqueId
                    _propertyRes.value = Result.Success(body)
                } else {
                    _propertyRes.value = Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _propertyRes.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }

        return true
    }

    fun submitPropertyKyc(context: Context): Boolean {
        if (!validateImage()) {
            Toast.makeText(context, "कृपया संपत्ति की फोटो अपलोड करें", Toast.LENGTH_SHORT).show()
            return false
        }
        viewModelScope.launch {
            _kycRes.value = Result.Loading
            try {
                val req = PropertyKycReq(
                    uniqueId = registeredUniqueId,
                    image = propertyImage?.base64 ?: ""
                )
                val response = repository.propertyKyc(req)
                _kycRes.value = if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                _kycRes.value = Result.Error(errorMessage = e.message ?: "Unexpected error")
            }
        }
        return true
    }

    fun resetRegistrationState() {
        _propertyRes.value = Result.Idle
        _kycRes.value = Result.Idle
        registeredUniqueId = ""
        wardId = null
        selectedWard.value = null
        constructionTypeId = null
        pconstructionTypeId = null
        selectedConstructionType.value = null
        selectedpConstructionType.value = null
        selectedRoadWide.value = null
        nonGovId = null
        selectedNonGov.value = null
        govId = null
        selectedGov.value = null
        mohallaId = null
        selectedMohalla.value = null
        sequence = null
        sequenceNo = TextFieldValue("")
        propertyNo = TextFieldValue("")
        ownerPropertyName = TextFieldValue("")
        fatherHusbandOwner = TextFieldValue("")
        contactNumber = TextFieldValue("")
        address = TextFieldValue("")
        residential = TextFieldValue("")
        commercial = TextFieldValue("")
        emptyArea = TextFieldValue("")
        totalArea = TextFieldValue("")
        propertyImage = null
        property = null
        propertyPos = null
        bhukhandPlat = null
        talabPokharaEtc = null
        propertyType = null
        propertyTypePos = null
        govNonGov = null
        govNonGovPos = null
        relation = null
        gender = null
        religion = null
        roadWide = null
        floorNo = null

        // Clear errors
        sequenceNoError = null
        propertyNoError = null
        ownerPropertyNameError = null
        fatherHusbandOwnerError = null
        contactNumberError = null
        addressError = null
        residentialError = null
        commercialError = null
        emptyAreaError = null
        totalAreaError = null
        propertyImageError = null
        propertyError = null
        bhukhandPlatError = null
        talabPokharaEtcError = null
        propertyTypeError = null
        govNonGovError = null
        relationError = null
        genderError = null
        religionError = null
        roadWideError = null
        floorNoError = null
        wardError = null
        mohallaError = null
        govError = null
        nonGovError = null
        constructionTypeError = null
        pconstructionTypeError = null
    }

    private fun getDeviceIpAddress(): String {
        try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                val addrs = intf.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                        return addr.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (ignored: Exception) {
        }
        return "127.0.0.1"
    }
}
