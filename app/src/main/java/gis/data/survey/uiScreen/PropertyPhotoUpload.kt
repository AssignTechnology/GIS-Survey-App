package gis.data.survey.uiScreen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import gis.data.survey.component.AppTopBar
import gis.data.survey.component.ImagePicker
import gis.data.survey.component.ImagePickerSection
import gis.data.survey.navigation.Screen
import gis.data.survey.viewModel.PropertyRegistrationViewModel
import gis.data.survey.model.responseModel.PropertyKycRes
import gis.data.survey.component.Result

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyPhotoUpload(
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
    var selectedImageUri by remember { mutableStateOf(viewModel.propertyImage?.uri) }
    var showImagePicker by remember { mutableStateOf(false) }
    var showCameraPreview by remember { mutableStateOf(false) }
    val kycRes by viewModel.kycRes.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var responseInfo by remember { mutableStateOf("") }
    var responseMessage by remember { mutableStateOf("") }

    LaunchedEffect(kycRes) {
        when (kycRes) {
            is Result.Success -> {
                val res = (kycRes as Result.Success<PropertyKycRes>).data
                if (res.np.responseCode == "200") {
                    responseInfo = res.np.uniqueId
                    responseMessage = res.np.responseMessage
                    showSuccessDialog = true
                } else {
                    Toast.makeText(context, res.np.responseMessage, Toast.LENGTH_LONG).show()
                }
            }
            is Result.Error -> {
                Toast.makeText(context, "त्रुटि: ${(kycRes as Result.Error).errorMessage}", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    AppTopBar(
        title = "Property Photo Upload",
        onNavigationIconClick = {
            navController.popBackStack()
        },
        rememberNavController = navController,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "संम्पत्ति की फोटो अपलोड करें *",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
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
                        onShowCameraPreview = { showCameraPreview = true },
                        onEditImage = { showImagePicker = true }
                    )
                    viewModel.propertyImageError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            style = TextStyle(fontSize = 12.sp),
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (viewModel.validateImage()) {
                                viewModel.submitPropertyKyc(context)
                            } else {
                                Toast.makeText(context, "कृपया संपत्ति की फोटो अपलोड करें", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = kycRes !is Result.Loading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(
                                width = 1.5.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        if (kycRes is Result.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "NEXT",
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    )

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(text = "पंजीकरण सफल!", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            },
            text = {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = responseMessage.ifBlank { "Kyc Successfully Updated" }, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        Text(text = "UniqueId / ID: ", fontWeight = FontWeight.Bold)
                        Text(
                            text = responseInfo,
                            color = Color(0xFF1B5E20),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                            viewModel.resetRegistrationState()
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Main.route) {
                                    inclusive = false
                                }
                            }
                        }
                    ) {
                        Text("Back to Home", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                            viewModel.resetRegistrationState()
                            navController.navigate(Screen.PropertyRegistration.route) {
                                popUpTo(Screen.PropertyRegistration.route) {
                                    inclusive = false
                                }
                            }
                        }
                    ) {
                        Text("Back to Form", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }

    if (showImagePicker) {
        ImagePicker.ShowImagePicker(
            showBottomSheet = true,
            onDismiss = { showImagePicker = false },
            onImageSelected = { uri ->
                selectedImageUri = uri?.uri
                viewModel.updatePropertyImage(uri)
                showImagePicker = false
            },
            onCameraSelected = {
                showImagePicker = false
                showCameraPreview = true
            }
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
