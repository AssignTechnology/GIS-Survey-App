package gis.data.survey.uiScreen


import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import gis.data.survey.component.AppTopBar
import gis.data.survey.viewModel.PropertyRegistrationViewModel
import gis.data.survey.model.responseModel.PropertyRes
import gis.data.survey.navigation.Screen
import gis.data.survey.component.Result


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyRegistrationPreviewScreen(
    navController: NavHostController,
    viewModel: PropertyRegistrationViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(
        remember(navController.currentBackStackEntry) {
            val entry = try {
                navController.getBackStackEntry(Screen.PropertyRegistration.route)
            } catch (_: Exception) {
                null
            }
            entry ?: navController.currentBackStackEntry!!
        }
    )
) {
    val context = LocalContext.current
    val propertyRes by viewModel.propertyRes.collectAsState()

    AppTopBar(
        title = "Preview",
        onNavigationIconClick = {
            navController.popBackStack()
        },
        rememberNavController = navController,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(it)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PreviewField("वार्ड", viewModel.selectedWard.value?.wardNo ?: "Not selected")
                    PreviewField(
                        "मोहल्ला",
                        viewModel.selectedMohalla.value?.mohallaName ?: "Not selected"
                    )
                    PreviewField("क्रम कोड", viewModel.sequence ?: "Not available")
                    PreviewField("क्रम संख्या", viewModel.sequenceNo.text)

                    PreviewField(
                        "संम्पत्ति के प्रकार",
                        viewModel.selectedPropertyType.value?.propertyType ?: viewModel.propertyType ?: "Not selected"
                    )
                    PreviewField(
                        "संम्पत्ति",
                        viewModel.selectedBoundProperty.value?.displayName ?: viewModel.property ?: "Not selected"
                    )

                    if (viewModel.selectedSubProperty.value != null) {
                        PreviewField("उप-संपत्ति (Sub-Property)", viewModel.selectedSubProperty.value?.subPropertyType ?: "")
                    }

                    if (!viewModel.govNonGov.isNullOrBlank()) {
                        PreviewField("सरकारी/गैर सरकारी", viewModel.govNonGov ?: "Not selected")
                    }

                    if (!viewModel.floorNo.isNullOrBlank()) {
                        PreviewField("फ्लोर नं", viewModel.floorNo ?: "Not selected")
                    }

                    if (viewModel.selectedpConstructionType.value != null) {
                        PreviewField("निर्माण के प्रकार", viewModel.selectedpConstructionType.value?.name ?: "Not selected")
                    }

                    if (viewModel.selectedConstructionType.value != null) {
                        PreviewField("उपनिर्माण के प्रकार", viewModel.selectedConstructionType.value?.name ?: "Not selected")
                    }

                    if (viewModel.residential.text.isNotBlank()) {
                        PreviewField("आवासीय क्षेत्रफल", viewModel.residential.text)
                    }
                    if (viewModel.commercial.text.isNotBlank()) {
                        PreviewField("व्यावसायिक क्षेत्रफल", viewModel.commercial.text)
                    }
                    if (viewModel.emptyArea.text.isNotBlank()) {
                        PreviewField("खाली क्षेत्रफल", viewModel.emptyArea.text)
                    }
                    if (viewModel.totalArea.text.isNotBlank()) {
                        PreviewField("सम्पूर्ण क्षेत्रफल", viewModel.totalArea.text)
                    }

                    if (viewModel.talabPokharaEtc != null) {
                        PreviewField("तालाब/पोखरा/कुआँ/नहर/इत्यादि", viewModel.talabPokharaEtc ?: "Not selected")
                    }

                    PreviewField("संम्पत्ति संख्या", viewModel.propertyNo.text)
                    PreviewField("मुखिया/संम्पत्ति का नाम", viewModel.ownerPropertyName.text)
                    PreviewField("सम्बंध", viewModel.relation ?: "Not selected")
                    PreviewField("पिता/पति/मालिक का नाम", viewModel.fatherHusbandOwner.text)
                    PreviewField("मोबाइल नं", viewModel.contactNumber.text)
                    PreviewField("पता", viewModel.address.text)
                    PreviewField("रोड की चौड़ाई", viewModel.selectedRoadWide.value?.roadWide ?: viewModel.roadWide ?: "Not selected")

                    viewModel.propertyImage?.uri?.let { uri ->
                        Text(
                            text = "संम्पत्ति की फोटो अपलोड करें",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        AsyncImage(
                            model = uri,
                            contentDescription = "Property Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.FillBounds
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
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
                                .padding(end = 8.dp)
                                .height(50.dp)
                        ) {
                            Text("Edit", style = MaterialTheme.typography.bodyLarge)
                        }

                        Button(
                            onClick = {
                                viewModel.submitForm(context, isPreview = false)
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
                                .weight(1f)
                                .padding(start = 8.dp)
                                .height(50.dp)
                        ) {
                            if (propertyRes is Result.Loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text("Next", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }

            LaunchedEffect(propertyRes) {
                when (propertyRes) {
                    is Result.Success -> {
                        val res = (propertyRes as Result.Success<PropertyRes>).data
                        if (res.responseCode == "200") {
                            navController.navigate(Screen.PropertyPhotoUpload.route)
                        } else {
                            Toast.makeText(context, res.responseMessage, Toast.LENGTH_LONG)
                                .show()
                        }
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
        }
    )
}

@Composable
fun PreviewField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom =.4.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                .padding(12.dp)
        )
    }
}
