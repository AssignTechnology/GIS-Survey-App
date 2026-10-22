package gis.data.survey.uiScreen

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gis.data.survey.model.responseModel.ConstructionTypeRes
import androidx.navigation.NavHostController
import gis.data.survey.component.AppTopBar
import gis.data.survey.component.CircularProgress
import gis.data.survey.component.DropdownMenu
import gis.data.survey.component.GetCurrentAddress
import gis.data.survey.component.ImagePicker
import gis.data.survey.component.ImagePickerSection
import gis.data.survey.component.TitledTextField
import gis.data.survey.viewModel.PropertyRegistrationViewModel
import gis.data.survey.navigation.Screen
import gis.data.survey.component.Result

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyRegistration(
    navController: NavHostController,
    viewModel: PropertyRegistrationViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
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
        viewModel.fetchPropertyTypes()
        viewModel.fetchFloorNos()
        viewModel.fetchPConstructionTypes()
        viewModel.fetchGovProperties()
        viewModel.fetchNonGovProperties()
        viewModel.fetchRoadWide()
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
                    else -> {}
                }

                val mohallaState = viewModel.mohallaState.collectAsState().value
                when (mohallaState) {
                    is Result.Loading -> CircularProgress()
                    is Result.Success -> {
                        val mohallaList = mohallaState.data.nP.responseData

                        // 👇 Auto-select first mohalla if not already selected
                        LaunchedEffect(mohallaList) {
                            if (mohallaList.isNotEmpty() && viewModel.selectedMohalla.value == null) {
                                val firstMohalla = mohallaList[0]
                                viewModel.selectedMohalla.value = firstMohalla
                                viewModel.updateMohallaId(firstMohalla.id)
                            }
                        }

                        DropdownMenu(
                            title = "मोहल्ला",
                            label = "मोहल्ला चुनें",
                            items = mohallaList,
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
//                    is Result.Success -> {
//                        DropdownMenu(
//                            title = "मोहल्ला",
//                            label = "मोहल्ला चुनें",
//                            items = mohallaState.data.nP.responseData,
//                            selectedItem = viewModel.selectedMohalla.value,
//                            onItemSelected = { pos, item ->
//                                viewModel.updateMohallaId(item.id)
//                                viewModel.selectedMohalla.value = item
//                            },
//                            displayText = { it.mohallaName },
//                            modifier = Modifier.fillMaxWidth(),
//                            error = viewModel.mohallaError
//                        )
//                    }
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

                // Property Type Dropdown (Fetched from API - at the top)
                val propertyTypeState = viewModel.propertyTypeState.collectAsState().value
                when (propertyTypeState) {
                    is Result.Loading -> CircularProgress()
                    is Result.Success -> {
                        DropdownMenu(
                            title = "संम्पत्ति के प्रकार *",
                            label = "संम्पत्ति के प्रकार चुनें",
                            items = propertyTypeState.data.nP.responseData,
                            selectedItem = viewModel.selectedPropertyType.value,
                            onItemSelected = { _, item ->
                                viewModel.selectPropertyType(item)
                            },
                            displayText = { it.propertyType },
                            modifier = Modifier.fillMaxWidth(),
                            error = viewModel.propertyTypeError
                        )
                    }
                    else -> {}
                }

                // Property Dropdown (Fetched via BindPropertyType using RCMPropertyId)
                val bindPropertyState = viewModel.bindPropertyTypeState.collectAsState().value
                when (bindPropertyState) {
                    is Result.Loading -> CircularProgress()
                    is Result.Success -> {
                        val propertyList = bindPropertyState.data.nP.responseData

                        // 👇 Auto-select first property if not already selected
                        LaunchedEffect(propertyList) {
                            if (propertyList.isNotEmpty() && viewModel.selectedBoundProperty.value == null) {
                                val firstProperty = propertyList[0]
                                viewModel.selectedBoundProperty.value = firstProperty
                            }
                        }

                        DropdownMenu(
                            title = "संम्पत्ति",
                            label = "संम्पत्ति चुनें",
                            items = propertyList,
                            selectedItem = viewModel.selectedBoundProperty.value,
                            onItemSelected = { _, item ->
                                viewModel.selectedBoundProperty.value = item
                            },
                            displayText = { it.displayName },
                            modifier = Modifier.fillMaxWidth(),
                            error = viewModel.propertyError,
                            enabled = false
                        )
                    }
                    else -> {}
                }

                // Sub-Property Type Dropdown (Visible ONLY when selectedPropertyType.value?.typeId == 1, e.g. Bank & ATM)
                if (viewModel.selectedPropertyType.value?.typeId == 1) {
                    val subPropState = viewModel.subPropertyState.collectAsState().value
                    when (subPropState) {
                        is Result.Success -> {
                            val subList = subPropState.data.nP.responseData
                            if (subList.isNotEmpty()) {
                                DropdownMenu(
                                    title = "उप-संपत्ति (Sub-Property Type)",
                                    label = "उप-संपत्ति चुनें",
                                    items = subList,
                                    selectedItem = viewModel.selectedSubProperty.value,
                                    onItemSelected = { _, item ->
                                        viewModel.updateSubProperty(item)
                                    },
                                    displayText = { it.subPropertyType },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        is Result.Loading -> CircularProgress()
                        else -> {
                            LaunchedEffect(viewModel.selectedPropertyType.value?.id) {
                                viewModel.fetchSubPropertyTypes(viewModel.selectedPropertyType.value?.id.toString())
                            }
                        }
                    }
                }

                // Conditional fields based on propertyTypePos
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

                    2 -> { // भूखंड/प्लाट
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

                    3 -> { // मिश्रित
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

                // Gov/Non-Gov Dropdown (Visible ONLY when selectedPropertyType.value?.govPri == 1)
                if (viewModel.selectedPropertyType.value?.govPri == 1) {
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
                                            viewModel.selectNonGovProperty(item)
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
                }

                // Floor Number Dropdown (Visible ONLY when propertyTypePos == 0 -> आवासीय)
                if (viewModel.propertyTypePos == 0) {
                    val floorState = viewModel.floorState.collectAsState().value
                    when (floorState) {
                        is Result.Success -> {
                            DropdownMenu(
                                title = "फ्लोर नं",
                                label = "फ्लोर नं चुनें",
                                items = floorState.data.nP.responseData,
                                selectedItem = viewModel.selectedFloor.value,
                                onItemSelected = { _, item ->
                                    viewModel.updateFloor(item)
                                },
                                displayText = { it.floorNo },
                                modifier = Modifier.fillMaxWidth(),
                                error = viewModel.floorNoError
                            )
                        }
                        is Result.Loading -> CircularProgress()
                        else -> {
                            LaunchedEffect(Unit) {
                                viewModel.fetchFloorNos()
                            }
                            CircularProgress()
                        }
                    }
                }
                val pconstructionTypeState =
                    viewModel.pconstructionType.collectAsState().value
                val cTypeId = viewModel.selectedPropertyType.value?.cTypeId ?: 0

                val defaultPConstList = listOf<ConstructionTypeRes.NP.ResponseData>(
                    ConstructionTypeRes.NP.ResponseData("1", "भूमि/प्लाט"),
                    ConstructionTypeRes.NP.ResponseData("2", "पक्का"),
                    ConstructionTypeRes.NP.ResponseData("3", "कच्चा + पक्का"),
                    ConstructionTypeRes.NP.ResponseData("4", "कच्चा")
                )
                val pConstItems = (pconstructionTypeState as? Result.Success)?.data?.nP?.responseData ?: defaultPConstList

                DropdownMenu(
                    title = "निर्माण के प्रकार *",
                    label = "निर्माण के प्रकार चुनें",
                    items = pConstItems,
                    selectedItem = viewModel.selectedpConstructionType.value,
                    onItemSelected = { _, item ->
                        viewModel.updatepConstructionTypeId(item.id)
                        viewModel.selectedpConstructionType.value = item
                        viewModel.updateConstructionTypeId("")
                        viewModel.fetchConstructionTypes(item.id)
                    },
                    displayText = { it.name },
                    modifier = Modifier.fillMaxWidth(),
                    isItemEnabled = { item ->
                        if (cTypeId == 0) {
                            item.id != "1" // Disable plot (id 1) when CTypeId is 0
                        } else {
                            item.id == "1" // Enable only plot (id 1) when CTypeId is 1
                        }
                    },
                    error = viewModel.pconstructionTypeError
                )

                val constructionTypeState =
                    viewModel.constructionType.collectAsState().value
                when (constructionTypeState) {
                    is Result.Loading -> CircularProgress()
                    is Result.Success -> {
                        DropdownMenu(
                            title = "उपनिर्माण के प्रकार",
                            label = "उपनिर्माण के प्रकार चुनें",
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
                    title = "संम्पत्ति/मकान संख्या",
                    value = viewModel.propertyNo,
                    onValueChange = viewModel::updatePropertyNo,
                    placeholder = "संम्पत्ति संख्या दर्ज करें",
                    error = viewModel.propertyNoError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
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



                TitledTextField(
                    title = "मोबाइल नं",
                    value = viewModel.contactNumber,  // ये TextFieldValue होना चाहिए
                    onValueChange = { input ->         // input अब TextFieldValue है
                        // सिर्फ़ डिजिट्स रखो + max 10
                        val builder = StringBuilder()
                        for (char in input.text) {     // <-- यहीं fix: input.text (क्योंकि TextFieldValue है)
                            if (char.isDigit()) {
                                builder.append(char)
                                if (builder.length >= 10) break
                            }
                        }
                        val filteredText = builder.toString()

                        // नया TextFieldValue बनाओ – कर्सर हमेशा लास्ट में रहेगा (सबसे अच्छा UX फ़ोन नंबर के लिए)
                        val newValue = input.copy(
                            text = filteredText,
                            selection = TextRange(filteredText.length)  // कर्सर end में
                            // composition = input.composition  // अगर mask यूज़ कर रहे हो तो ये भी copy कर लो
                        )

                        viewModel.updateContactNumber(newValue)
                    },
                    placeholder = "मोबाइल नं दर्ज करें",
                    error = viewModel.contactNumberError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    maxLines = 1
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


                val roadWideState =
                    viewModel.roadWideArea.collectAsState().value
                when (roadWideState) {
                    is Result.Loading -> CircularProgress()
                    is Result.Success -> {
                        DropdownMenu(
                            title = "रोड की चौड़ाई *",
                            label = "रोड की चौड़ाई चुनें",
                            items = roadWideState.data.nP.responseData,
                            selectedItem = viewModel.selectedRoadWide.value,
                            onItemSelected = { _, item ->
                                viewModel.updateRoadWide(item.roadWide)
                                viewModel.selectedRoadWide.value = item
                            },
                            displayText = { it.roadWide },
                            modifier = Modifier.fillMaxWidth(),
                            error = viewModel.roadWideError
                        )
                    }
                    is Result.Error -> {}
                    else -> {
                        LaunchedEffect(Unit) {
                            viewModel.fetchRoadWide()
                        }
                        CircularProgress()
                    }
                }
//
//                DropdownMenu(
//                    title = "रोड की चौड़ाई",
//                    label = "रोड की चौड़ाई चुनें",
//                    items = listOf("10 फिट से कम", "10 से 15 फिट तक", "15 फिट से अधिक"),
//                    selectedItem = viewModel.roadWide,
//                    onItemSelected = { _, item -> viewModel.updateRoadWide(item) },
//                    displayText = { it },
//                    modifier = Modifier.fillMaxWidth(),
//                    error = viewModel.roadWideError
//                )

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Back", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold))
                }

                Button(
                    onClick = {
                        if (viewModel.validateForm(context)) {
                            navController.navigate(Screen.PropertyRegistrationPreview.route)
                        } else {
                            Toast.makeText(context, "कृपया सभी आवश्यक फ़ील्ड भरें", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .border(
                            width = 1.5.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text(
                        "Preview",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}











