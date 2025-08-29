package com.example.npassigntech.uiScreen

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.npassigntech.component.AppTopBar
import com.example.npassigntech.component.CircularProgress
import com.example.npassigntech.component.DropdownMenu
import com.example.npassigntech.component.GetCurrentAddress
import com.example.npassigntech.component.ImagePicker
import com.example.npassigntech.component.ImagePickerSection
import com.example.npassigntech.component.TitledTextField
import com.example.npassigntech.viewModel.PropertyRegistrationViewModel
import com.example.npassigntech.component.Result
import com.example.npassigntech.navigation.Screen

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyRegistration(
    navController: NavHostController,
    viewModel: PropertyRegistrationViewModel = hiltViewModel()
) {
    AppTopBar(
        title = "Property Registration",
        useMediumTopAppBar = true,
        onNavigationIconClick = {
            navController.popBackStack()
        },
        rememberNavController = navController,
        content = {
            PropertyScreen(it, viewModel, navController)
        }
    )

}


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyScreen(
    values: PaddingValues,
    viewModel: PropertyRegistrationViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePicker by remember { mutableStateOf(false) }
    var showCameraPreview by remember { mutableStateOf(false) }
    var currentAddress by remember { mutableStateOf<String?>(null) }

    val propertyRes by viewModel.propertyRes.collectAsState()
    LaunchedEffect(propertyRes) {
        when (propertyRes) {
            is Result.Success -> {
                Toast.makeText(context, "प्रॉपर्टी सफलतापूर्वक दर्ज की गई", Toast.LENGTH_LONG)
                    .show()
            }

            is Result.Error -> {
                Toast.makeText(
                    context,
                    "त्रुटि: ${(propertyRes as Result.Error).errorMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchWards()
        viewModel.fetchConstructionTypes()
        viewModel.fetchGovProperties()
        viewModel.fetchNonGovProperties()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(values)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ward Dropdown
                val wardState = viewModel.wardState.collectAsState().value
                when (wardState) {
                    is Result.Loading -> CircularProgress()
                    is Result.Success -> {
                        DropdownMenu(
                            title = "वार्ड",
                            label = "वार्ड चुनें",
                            items = wardState.data.nP.responseData,
                            selectedItem = viewModel.selectedWard.value,
                            onItemSelected = { pos, item ->
                                viewModel.updateWardId(item.id.toString())
                                viewModel.selectedWard.value = item
                                viewModel.updateMohallaId("")
                                viewModel.selectedMohalla.value = null
                                viewModel.fetchMohalla(item.id)
                                viewModel.fetchSequence(item.id.toString())
                            },
                            displayText = { it.wardNo },
                            modifier = Modifier.fillMaxWidth(),
                            error = viewModel.wardError
                        )
                    }
                    is Result.Error -> Text("Error: ${wardState.errorMessage}", color = Color.Red)
                    else -> {}
                }

                val mohallaState = viewModel.mohallaState.collectAsState().value
                when (mohallaState) {
                    is Result.Loading -> CircularProgress()
                    is Result.Success -> {
                        DropdownMenu(
                            title = "मोहल्ला",
                            label = "मोहल्ला चुनें",
                            items = mohallaState.data.nP.responseData,
                            selectedItem = viewModel.selectedMohalla.value,
                            onItemSelected = { pos, item ->
                                viewModel.updateMohallaId(item.id)
                                viewModel.selectedMohalla.value = item
                            },
                            displayText = { it.mohallaName },
                            modifier = Modifier.fillMaxWidth(),
                            error = viewModel.mohallaError
                        )
                    }
                    is Result.Error -> Text(
                        "Error: ${mohallaState.errorMessage}",
                        color = Color.Red
                    )
                    else -> {}
                }
                // Sequence
                val sequenceState = viewModel.sequenceState.collectAsState().value
                when (sequenceState) {
                    is Result.Loading -> CircularProgress()
                    is Result.Success -> {
                        TitledTextField(
                            title = "क्रम कोड",
                            value = TextFieldValue(
                                sequenceState.data.nP.responseData.sequence
                            ),
                            onValueChange = {},
                            placeholder = "क्रम कोड",
                            error = null,
                            readOnly = true,
                        )
                        viewModel.updateSequence(sequenceState.data.nP.responseData.sequence)
                    }

                    else -> {}
                }
                TitledTextField(
                    title = "क्रम संख्या",
                    value = viewModel.sequenceNo,
                    onValueChange = viewModel::updateSequenceNo,
                    placeholder = "क्रम संख्या दर्ज करें",
                    error = viewModel.sequenceNoError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Property Selection
                DropdownMenu(
                    title = "संम्पत्ति",
                    label = "संम्पत्ति चुनें",
                    items = listOf("भूखण्ड/प्लाट", "तालाब/पोखरा/कुआँ/नहर/इत्यादि", "संम्पत्ति"),
                    selectedItem = viewModel.property,
                    onItemSelected = { pos, item ->
                        viewModel.updateProperty(item, pos)
                    },
                    displayText = { it },
                    modifier = Modifier.fillMaxWidth(),
                    error = viewModel.propertyError
                )

                // Conditional fields based on property
                when (viewModel.propertyPos) {
                    0 -> { // भूखण्ड/प्लाट
                        DropdownMenu(
                            title = "भूखण्ड/प्लाट",
                            label = "भूखण्ड/प्लाट चुनें",
                            items = listOf("भूखण्ड/प्लाट"), // Replace with actual options
                            selectedItem = viewModel.bhukhandPlat,
                            onItemSelected = { _, item -> viewModel.updateBhukhandPlat(item) },
                            displayText = { it },
                            modifier = Modifier.fillMaxWidth(),
                            error = viewModel.bhukhandPlatError
                        )
                        TitledTextField(
                            title = "आवासीय क्षेत्रफल",
                            value = viewModel.residential,
                            onValueChange = viewModel::updateResidential,
                            placeholder = "आवासीय क्षेत्रफल दर्ज करें",
                            error = viewModel.residentialError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        TitledTextField(
                            title = "व्यावसायिक क्षेत्रफल",
                            value = viewModel.commercial,
                            onValueChange = viewModel::updateCommercial,
                            placeholder = "व्यावसायिक क्षेत्रफल दर्ज करें",
                            error = viewModel.commercialError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        TitledTextField(
                            title = "खाली क्षेत्रफल",
                            value = viewModel.emptyArea,
                            onValueChange = viewModel::updateEmptyArea,
                            placeholder = "खाली क्षेत्रफल दर्ज करें",
                            error = viewModel.emptyAreaError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        TitledTextField(
                            title = "सम्पूर्ण क्षेत्रफल",
                            value = viewModel.totalArea,
                            onValueChange = viewModel::updateTotalArea,
                            placeholder = "सम्पूर्ण क्षेत्रफल दर्ज करें",
                            error = viewModel.totalAreaError,
                            readOnly = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    1 -> { // तालाब/पोखरा/etc
                        DropdownMenu(
                            title = "तालाब/पोखरा/कुआँ/नहर/इत्यादि",
                            label = "चुनें",
                            items = listOf(
                                "तालाब",
                                "पोखरा",
                                "कुआँ",
                                "नहर",
                                "इत्यादि"
                            ), // Replace if needed
                            selectedItem = viewModel.talabPokharaEtc,
                            onItemSelected = { _, item -> viewModel.updateTalabPokharaEtc(item) },
                            displayText = { it },
                            modifier = Modifier.fillMaxWidth(),
                            error = viewModel.talabPokharaEtcError
                        )
                    }

                    2 -> { // संम्पत्ति
                        DropdownMenu(
                            title = "संम्पत्ति के प्रकार",
                            label = "संम्पत्ति के प्रकार चुनें",
                            items = listOf("आवासीय", "व्यवसायिक", "मिश्रित"),
                            selectedItem = viewModel.propertyType,
                            onItemSelected = { pos, item ->
                                viewModel.updatePropertyType(item, pos)
                            },
                            displayText = { it },
                            modifier = Modifier.fillMaxWidth(),
                            error = viewModel.propertyTypeError
                        )

                        // Conditional based on propertyType
                        when (viewModel.propertyTypePos) {
                            0 -> { // आवासीय
                                TitledTextField(
                                    title = "आवासीय क्षेत्रफल",
                                    value = viewModel.residential,
                                    onValueChange = viewModel::updateResidential,
                                    placeholder = "आवासीय क्षेत्रफल दर्ज करें",
                                    error = viewModel.residentialError,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                TitledTextField(
                                    title = "खाली क्षेत्रफल",
                                    value = viewModel.emptyArea,
                                    onValueChange = viewModel::updateEmptyArea,
                                    placeholder = "खाली क्षेत्रफल दर्ज करें",
                                    error = viewModel.emptyAreaError,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                TitledTextField(
                                    title = "सम्पूर्ण क्षेत्रफल",
                                    value = viewModel.totalArea,
                                    onValueChange = viewModel::updateTotalArea,
                                    placeholder = "सम्पूर्ण क्षेत्रफल दर्ज करें",
                                    error = viewModel.totalAreaError,
                                    readOnly = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }

                            1 -> { // व्यवसायिक
                                TitledTextField(
                                    title = "व्यावसायिक क्षेत्रफल",
                                    value = viewModel.commercial,
                                    onValueChange = viewModel::updateCommercial,
                                    placeholder = "व्यावसायिक क्षेत्रफल दर्ज करें",
                                    error = viewModel.commercialError,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                TitledTextField(
                                    title = "खाली क्षेत्रफल",
                                    value = viewModel.emptyArea,
                                    onValueChange = viewModel::updateEmptyArea,
                                    placeholder = "खाली क्षेत्रफल दर्ज करें",
                                    error = viewModel.emptyAreaError,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                TitledTextField(
                                    title = "सम्पूर्ण क्षेत्रफल",
                                    value = viewModel.totalArea,
                                    onValueChange = viewModel::updateTotalArea,
                                    placeholder = "सम्पूर्ण क्षेत्रफल दर्ज करें",
                                    error = viewModel.totalAreaError,
                                    readOnly = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }

                            2 -> { // मिश्रित
                                TitledTextField(
                                    title = "आवासीय क्षेत्रफल",
                                    value = viewModel.residential,
                                    onValueChange = viewModel::updateResidential,
                                    placeholder = "आवासीय क्षेत्रफल दर्ज करें",
                                    error = viewModel.residentialError,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                TitledTextField(
                                    title = "व्यावसायिक क्षेत्रफल",
                                    value = viewModel.commercial,
                                    onValueChange = viewModel::updateCommercial,
                                    placeholder = "व्यावसायिक क्षेत्रफल दर्ज करें",
                                    error = viewModel.commercialError,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                TitledTextField(
                                    title = "खाली क्षेत्रफल",
                                    value = viewModel.emptyArea,
                                    onValueChange = viewModel::updateEmptyArea,
                                    placeholder = "खाली क्षेत्रफल दर्ज करें",
                                    error = viewModel.emptyAreaError,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                TitledTextField(
                                    title = "सम्पूर्ण क्षेत्रफल",
                                    value = viewModel.totalArea,
                                    onValueChange = viewModel::updateTotalArea,
                                    placeholder = "सम्पूर्ण क्षेत्रफल दर्ज करें",
                                    error = viewModel.totalAreaError,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    readOnly = true
                                )
                            }
                        }

                        DropdownMenu(
                            title = "सरकारी/गैर सरकारी",
                            label = "सरकारी/गैर सरकारी चुनें",
                            items = listOf("सरकारी", "गैर सरकारी"),
                            selectedItem = viewModel.govNonGov,
                            onItemSelected = { pos, item ->
                                viewModel.updateGovNonGov(item, pos)
                            },
                            displayText = { it },
                            modifier = Modifier.fillMaxWidth(),
                            error = viewModel.govNonGovError
                        )

                        // Conditional based on govNonGov
                        when (viewModel.govNonGovPos) {
                            0 -> { // सरकारी
                                val govState = viewModel.gov.collectAsState().value
                                when (govState) {
                                    is Result.Loading -> CircularProgress()
                                    is Result.Success -> {
                                        DropdownMenu(
                                            title = "सरकारी संम्पत्ति",
                                            label = "सरकारी संम्पत्ति चुनें",
                                            items = govState.data.nP.responseData,
                                            selectedItem = viewModel.selectedGov.value,
                                            onItemSelected = { _, item ->
                                                viewModel.updateGovId(item.id.toString())
                                                viewModel.selectedGov.value = item
                                            },
                                            displayText = { it.govProperty },
                                            modifier = Modifier.fillMaxWidth(),
                                            error = viewModel.govError
                                        )
                                    }

                                    else -> {}
                                }
                            }

                            1 -> { // गैर सरकारी
                                val nonGovState = viewModel.nonGov.collectAsState().value
                                when (nonGovState) {
                                    is Result.Loading -> CircularProgress()
                                    is Result.Success -> {
                                        DropdownMenu(
                                            title = "गैर सरकारी संम्पत्ति",
                                            label = "गैर सरकारी संम्पत्ति चुनें",
                                            items = nonGovState.data.nP.responseData,
                                            selectedItem = viewModel.selectedNonGov.value,
                                            onItemSelected = { _, item ->
                                                viewModel.updateNonGovId(item.id.toString())
                                                viewModel.selectedNonGov.value = item
                                            },
                                            displayText = { it.privateProperty },
                                            modifier = Modifier.fillMaxWidth(),
                                            error = viewModel.nonGovError
                                        )
                                    }

                                    else -> {}
                                }
                            }
                        }

                        DropdownMenu(
                            title = "फ्लोर नं",
                            label = "फ्लोर नं चुनें",
                            items = listOf(
                                "1 तल",
                                "2 तल",
                                "3 तल",
                                "4 तल",
                                "4 से अधिक तल"
                            ), // Example
                            selectedItem = viewModel.floorNo,
                            onItemSelected = { _, item -> viewModel.updateFloorNo(item) },
                            displayText = { it },
                            modifier = Modifier.fillMaxWidth(),
                            error = viewModel.floorNoError
                        )

                        val constructionTypeState =
                            viewModel.constructionType.collectAsState().value
                        when (constructionTypeState) {
                            is Result.Loading -> CircularProgress()
                            is Result.Success -> {
                                DropdownMenu(
                                    title = "निर्माण के प्रकार",
                                    label = "निर्माण के प्रकार चुनें",
                                    items = constructionTypeState.data.nP.responseData,
                                    selectedItem = viewModel.selectedConstructionType.value,
                                    onItemSelected = { _, item ->
                                        viewModel.updateConstructionTypeId(item.id)
                                        viewModel.selectedConstructionType.value = item
                                    },
                                    displayText = { it.name },
                                    modifier = Modifier.fillMaxWidth(),
                                    error = viewModel.constructionTypeError
                                )
                            }

                            else -> {}
                        }

                        TitledTextField(
                            title = "निर्माण वर्ष",
                            value = viewModel.constructionYear,
                            onValueChange = viewModel::updateConstructionYear,
                            placeholder = "निर्माण वर्ष दर्ज करें",
                            error = viewModel.constructionYearError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                TitledTextField(
                    title = "संम्पत्ति संख्या",
                    value = viewModel.propertyNo,
                    onValueChange = viewModel::updatePropertyNo,
                    placeholder = "संम्पत्ति संख्या दर्ज करें",
                    error = viewModel.propertyNoError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TitledTextField(
                    title = "मुखिया/संम्पत्ति का नाम",
                    value = viewModel.ownerPropertyName,
                    onValueChange = viewModel::updateOwnerPropertyName,
                    placeholder = "मुखिया/संम्पत्ति का नाम दर्ज करें",
                    error = viewModel.ownerPropertyNameError,
                )

                DropdownMenu(
                    title = "सम्बंध",
                    label = "सम्बंध चुनें",
                    items = listOf("पुत्र", "पुत्री", "पत्नी", "मालिक"),
                    selectedItem = viewModel.relation,
                    onItemSelected = { _, item -> viewModel.updateRelation(item) },
                    displayText = { it },
                    modifier = Modifier.fillMaxWidth(),
                    error = viewModel.relationError
                )

                TitledTextField(
                    title = "पिता/पति/मालिक का नाम",
                    value = viewModel.fatherHusbandOwner,
                    onValueChange = viewModel::updateFatherHusbandOwner,
                    placeholder = "पिता/पति/मालिक का नाम दर्ज करें",
                    error = viewModel.fatherHusbandOwnerError
                )

                DropdownMenu(
                    title = "लिंग",
                    label = "लिंग चुनें",
                    items = listOf("पुरुष", "महिला", "अन्य"),
                    selectedItem = viewModel.gender,
                    onItemSelected = { _, item -> viewModel.updateGender(item) },
                    displayText = { it },
                    modifier = Modifier.fillMaxWidth(),
                    error = viewModel.genderError
                )

                DropdownMenu(
                    title = "धर्म",
                    label = "धर्म चुनें",
                    items = listOf("हिन्दू", "मुस्लिम", "सिख", "इसाई", "अन्य"),
                    selectedItem = viewModel.religion,
                    onItemSelected = { _, item -> viewModel.updateReligion(item) },
                    displayText = { it },
                    modifier = Modifier.fillMaxWidth(),
                    error = viewModel.religionError
                )

                TitledTextField(
                    title = "मोबाइल नं",
                    value = viewModel.contactNumber,
                    onValueChange = viewModel::updateContactNumber,
                    placeholder = "मोबाइल नं दर्ज करें",
                    error = viewModel.contactNumberError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                GetCurrentAddress { address ->
                    currentAddress = address
                }
                viewModel.address = TextFieldValue(currentAddress.toString())

                TitledTextField(
                    title = "पता",
                    value = viewModel.address,
                    onValueChange = viewModel::updateAddress,
                    placeholder = "पता दर्ज करें",
                    error = viewModel.addressError,
                    readOnly = true,

                    )




                DropdownMenu(
                    title = "रोड की चौड़ाई",
                    label = "रोड की चौड़ाई चुनें",
                    items = listOf("10 फिट से कम", "10 से 15 फिट तक", "15 फिट से अधिक"),
                    selectedItem = viewModel.roadWide,
                    onItemSelected = { _, item -> viewModel.updateRoadWide(item) },
                    displayText = { it },
                    modifier = Modifier.fillMaxWidth(),
                    error = viewModel.roadWideError
                )

                // Image Picker
                Column {
                    Text(
                        text = "संम्पत्ति की फोटो अपलोड करें",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    ImagePickerSection(
                        selectedImageUri = selectedImageUri,
                        onImageSelected = { uri ->
                            selectedImageUri = uri.uri
                            viewModel.updatePropertyImage(uri)
                        },
                        onShowImagePicker = { showImagePicker = true },
                        onClearImage = {
                            selectedImageUri = null
                            viewModel.updatePropertyImage(null)
                        },
                        onShowCameraPreview = { showCameraPreview = true }
                    )
                    viewModel.propertyImageError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            style = TextStyle(fontSize = 12.sp),
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (viewModel.submitForm(context, isPreview = true)) {
                        navController.navigate(Screen.PropertyRegistrationPreview.route)
                    }
                },
                enabled = propertyRes !is Result.Loading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(50.dp)
                    .border(
                        width = 1.5.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                if (propertyRes is Result.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "PREVIEW",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }

    if (showImagePicker) {
        ImagePicker.ShowImagePicker(
            showBottomSheet = true,
            onDismiss = { showImagePicker = false },
            onImageSelected = { uri ->
                selectedImageUri = uri?.uri
                viewModel.updatePropertyImage(uri)
            },
            onCameraSelected = { showCameraPreview = true }
        )
    }

    if (showCameraPreview) {
        ImagePicker.CameraPreviewScreen(
            onImageCaptured = { uri ->
                selectedImageUri = uri?.uri
                viewModel.updatePropertyImage(uri)
                showCameraPreview = false
            },
            onDismiss = { showCameraPreview = false }
        )
    }
}











