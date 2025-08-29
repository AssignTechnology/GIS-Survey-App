package com.example.npassigntech.uiScreen


import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.npassigntech.component.AppTopBar
import com.example.npassigntech.viewModel.PropertyRegistrationViewModel
import  com.example.npassigntech.component.Result
import com.example.npassigntech.navigation.Screen


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyRegistrationPreviewScreen(
    navController: NavHostController,
    viewModel: PropertyRegistrationViewModel = hiltViewModel(
        navController.getBackStackEntry(Screen.PropertyRegistration.route)
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
                    PreviewField("मोहल्ला", viewModel.selectedMohalla.value?.mohallaName ?: "Not selected")
                    PreviewField("क्रम कोड", viewModel.sequence ?: "Not available")
                    PreviewField("क्रम संख्या", viewModel.sequenceNo.text)
                    PreviewField("संम्पत्ति", viewModel.property ?: "Not selected")

                    when (viewModel.propertyPos) {
                        0 -> {
                            PreviewField("भूखण्ड/प्लाट", viewModel.bhukhandPlat ?: "Not selected")
                            PreviewField("आवासीय क्षेत्रफल", viewModel.residential.text)
                            PreviewField("व्यावसायिक क्षेत्रफल", viewModel.commercial.text)
                            PreviewField("खाली क्षेत्रफल", viewModel.emptyArea.text)
                            PreviewField("सम्पूर्ण क्षेत्रफल", viewModel.totalArea.text)
                        }
                        1 -> {
                            PreviewField("तालाब/पोखरा/कुआँ/नहर/इत्यादि", viewModel.talabPokharaEtc ?: "Not selected")
                        }
                        2 -> {
                            PreviewField("संम्पत्ति के प्रकार", viewModel.propertyType ?: "Not selected")
                            PreviewField("सरकारी/गैर सरकारी", viewModel.govNonGov ?: "Not selected")
                            when (viewModel.govNonGovPos) {
                                0 -> PreviewField(
                                    "सरकारी",
                                    viewModel.selectedGov.value?.govProperty ?: "Not selected"
                                )
                                1 -> PreviewField(
                                    "गैर सरकारी",
                                    viewModel.selectedNonGov.value?.privateProperty ?: "Not selected"
                                )
                            }
                            PreviewField("फ्लोर नं", viewModel.floorNo ?: "Not selected")
                            PreviewField(
                                "निर्माण के प्रकार",
                                viewModel.selectedConstructionType.value?.name ?: "Not selected"
                            )
                            PreviewField("निर्माण वर्ष", viewModel.constructionYear.text)
                            if (viewModel.propertyTypePos == 0 || viewModel.propertyTypePos == 2) {
                                PreviewField("आवासीय क्ष्रेत्र", viewModel.residential.text)
                            }
                            if (viewModel.propertyTypePos == 1 || viewModel.propertyTypePos == 2) {
                                PreviewField("व्यावसायिक क्ष्रेत्र", viewModel.commercial.text)
                            }
                            PreviewField("खाली क्ष्रेत्र", viewModel.emptyArea.text)
                            PreviewField("कुल क्ष्रेत्र", viewModel.totalArea.text)
                        }
                    }

                    PreviewField("संम्पत्ति संख्या", viewModel.propertyNo.text)
                    PreviewField("मुखिया/संम्पत्ति का नाम", viewModel.ownerPropertyName.text)
                    PreviewField("सम्बंध", viewModel.relation ?: "Not selected")
                    PreviewField("पिता/पति/मालिक का नाम", viewModel.fatherHusbandOwner.text)
                    PreviewField("लिंग", viewModel.gender ?: "Not selected")
                    PreviewField("धर्म", viewModel.religion ?: "Not selected")
                    PreviewField("मोबाइल नं", viewModel.contactNumber.text)
                    PreviewField("पता", viewModel.address.text)
                    PreviewField("रोड की चौड़ाई", viewModel.roadWide ?: "Not selected")

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
                            Text("Back", style = MaterialTheme.typography.bodyLarge)
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
                                Text("Submit", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }

            LaunchedEffect(propertyRes) {
                when (propertyRes) {
                    is Result.Success -> {

                        navController.navigate(Screen.PropertyRegistration.route) {
                            popUpTo(Screen.PropertyRegistrationPreview.route) {
                                inclusive = true
                            }
                        }
                        Toast.makeText(context, "Form Submitted Successfully", Toast.LENGTH_LONG).show()
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            context,
                            "Error: ${(propertyRes as Result.Error).errorMessage}",
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
            modifier = Modifier.padding(bottom = 4.dp)
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