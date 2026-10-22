package gis.data.survey.uiScreen

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import gis.data.survey.component.AppTopBar
import gis.data.survey.component.ImagePicker
import gis.data.survey.component.NoDataAnimation
import gis.data.survey.component.Result
import gis.data.survey.model.responseModel.PropertyReportModel
import gis.data.survey.sharedPreferences.SharedPreferencesManager
import gis.data.survey.viewModel.PropertyReportViewModel
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun PropertyReport(
    navController: NavHostController,
    viewModel: PropertyReportViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    val userPropertyReportState by viewModel.userPropertyReport.collectAsState()
    val context = LocalContext.current
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    viewModel.userNo = SharedPreferencesManager.getInstance(context).getValue("UserId", "").toString()
        .ifBlank { SharedPreferencesManager.getInstance(context).getValue("UserNumber", "").toString() }

    LaunchedEffect(viewModel.userNo) {
        if (viewModel.userNo.isNotEmpty()) {
            viewModel.fetchUserProperties(viewModel.userNo)
        }
    }

    var showCameraPreview by remember { mutableStateOf(false) }
    var onImageCapturedCallback: ((ImagePicker.ImageResult?) -> Unit)? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        PropertyDetailsScreen(
            userPropertyReportState = userPropertyReportState,
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            navController = navController,
            viewModel = viewModel,
            onOpenFullCamera = { callback ->
                onImageCapturedCallback = callback
                showCameraPreview = true
            }
        )

        if (showCameraPreview) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                ImagePicker.CameraPreviewScreen(
                    onImageCaptured = { imgRes ->
                        onImageCapturedCallback?.invoke(imgRes)
                        showCameraPreview = false
                    },
                    onDismiss = {
                        showCameraPreview = false
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailsScreen(
    userPropertyReportState: Result<PropertyReportModel>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    navController: NavHostController,
    viewModel: PropertyReportViewModel,
    onOpenFullCamera: ((ImagePicker.ImageResult?) -> Unit) -> Unit
) {
    var selectedWard by remember { mutableStateOf("All Wards") }
    var selectedMohalla by remember { mutableStateOf("All Mohallas") }
    var isWardExpanded by remember { mutableStateOf(false) }
    var isMohallaExpanded by remember { mutableStateOf(false) }

    AppTopBar(
        title = "Property Details",
        onNavigationIconClick = { navController.popBackStack() },
        rememberNavController = navController,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Dropdown Filters Section
                if (userPropertyReportState is Result.Success) {
                    val rawList = userPropertyReportState.data.nP.responseData
                    val wards = listOf("All Wards") + rawList.map { it.wardName }.distinct().sorted()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = isWardExpanded,
                            onExpandedChange = { isWardExpanded = !isWardExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedWard,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("वार्ड") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isWardExpanded) },
                                modifier = Modifier.menuAnchor(),
                                textStyle = TextStyle(fontSize = 12.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = isWardExpanded,
                                onDismissRequest = { isWardExpanded = false }
                            ) {
                                wards.forEach { ward ->
                                    DropdownMenuItem(
                                        text = { Text(ward) },
                                        onClick = {
                                            selectedWard = ward
                                            selectedMohalla = "All Mohallas"
                                            isWardExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        val mohallas = listOf("All Mohallas") + rawList
                            .filter { it.wardName == selectedWard || selectedWard == "All Wards" }
                            .map { it.mohallaName }.distinct().sorted()

                        ExposedDropdownMenuBox(
                            expanded = isMohallaExpanded,
                            onExpandedChange = { isMohallaExpanded = !isMohallaExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedMohalla,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("मोहल्ला") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMohallaExpanded) },
                                modifier = Modifier.menuAnchor(),
                                textStyle = TextStyle(fontSize = 12.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = isMohallaExpanded,
                                onDismissRequest = { isMohallaExpanded = false }
                            ) {
                                mohallas.forEach { mohalla ->
                                    DropdownMenuItem(
                                        text = { Text(mohalla) },
                                        onClick = {
                                            selectedMohalla = mohalla
                                            isMohallaExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (userPropertyReportState) {
                        is Result.Success -> {
                            val rawList = userPropertyReportState.data.nP.responseData

                            val filteredList = remember(selectedWard, selectedMohalla, rawList) {
                                rawList.filter { item ->
                                    val wardMatch = selectedWard == "All Wards" || item.wardName == selectedWard
                                    val mohallaMatch = selectedMohalla == "All Mohallas" || item.mohallaName == selectedMohalla
                                    wardMatch && mohallaMatch
                                }
                            }

                            Column {
                                Text(
                                    text = "कुल प्रॉपर्टी: ${filteredList.size}",
                                    modifier = Modifier.padding(10.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                if (filteredList.isEmpty()) {
                                    NoDataAnimation()
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(filteredList.size) { index ->
                                            PropertyDetailCard(filteredList[index], viewModel, onOpenFullCamera)
                                        }
                                    }
                                }
                            }
                        }
                        is Result.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is Result.Error -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Error loading data", color = MaterialTheme.colorScheme.error)
                            }
                        }
                        else -> {
                            NoDataAnimation()
                        }
                    }
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun PropertyDetailCard(
    data: PropertyReportModel.NP.ResponseData,
    viewModel: PropertyReportViewModel,
    onOpenFullCamera: ((ImagePicker.ImageResult?) -> Unit) -> Unit
) {
    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf("") }
    var showUpdateKycDialog by remember { mutableStateOf(false) }

    // KYC states
    var aadharFrontUri by remember { mutableStateOf<Uri?>(null) }
    var aadharFrontBase64 by remember { mutableStateOf("") }

    var aadharBackUri by remember { mutableStateOf<Uri?>(null) }
    var aadharBackBase64 by remember { mutableStateOf("") }

    var panCardUri by remember { mutableStateOf<Uri?>(null) }
    var panCardBase64 by remember { mutableStateOf("") }

    var regFormUri by remember { mutableStateOf<Uri?>(null) }
    var regFormBase64 by remember { mutableStateOf("") }

    var propertyPaperUri by remember { mutableStateOf<Uri?>(null) }
    var propertyPaperBase64 by remember { mutableStateOf("") }

    var aiftidevitUri by remember { mutableStateOf<Uri?>(null) }
    var aiftidevitBase64 by remember { mutableStateOf("") }

    var propertyImgUri by remember { mutableStateOf<Uri?>(null) }
    var propertyImgBase64 by remember { mutableStateOf("") }

    var activeField by remember { mutableStateOf<String?>(null) }
    var showImagePicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Owner Name & IDs
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.ownerName.ifBlank { "N/A" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Cust ID: ${data.uniqueId} | Property No: ${data.propertyNo}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // Details List
            getPropertyDetails(data).forEach { detail ->
                val isTarget = detail.first == "प्रॉपर्टी संख्या" || detail.first == "Uid संख्या"
                val targetColor = if (isTarget) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                val labelColor = if (isTarget) Color(0xFF1B5E20) else MaterialTheme.colorScheme.primary
                val fontWeight = if (isTarget) FontWeight.ExtraBold else FontWeight.Normal

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${detail.first}:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = labelColor,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = detail.second,
                        fontSize = 14.sp,
                        fontWeight = fontWeight,
                        color = targetColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Update KYC Button
            Button(
                onClick = { showUpdateKycDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6F00),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "दस्तावेज़ अपडेट करें (Update KYC)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            // Documents Section ("दस्तावेज़ देखें")
            Text(
                text = "दस्तावेज़ देखें",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )

            val documents = listOf(
                "आधार (सामने)" to data.aadharimg,
                "आधार (पीछे)" to data.aadharbackimg,
                "पैन कार्ड" to data.panCard,
                "रजिस्ट्रेशन फॉर्म" to data.formimg,
                "संपत्ति कागजात" to data.propertyimg,
                "शपथ पत्र" to data.aiftidevitpath,
                "संपत्ति फोटो" to (data.imageUrl.ifBlank { data.imagesUrl })
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(documents) { doc ->
                    val title = doc.first
                    val url = doc.second

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(90.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .size(90.dp)
                                .clickable {
                                    if (url.isNotBlank()) {
                                        selectedImageUrl = url
                                        showImageDialog = true
                                    } else {
                                        showUpdateKycDialog = true
                                    }
                                },
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            if (url.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(url)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(id = android.R.drawable.ic_menu_gallery),
                                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFFFF3E0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudUpload,
                                        contentDescription = title,
                                        tint = Color(0xFFFF6F00),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            maxLines = 1,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    // Zoom Image Dialog
    if (showImageDialog && selectedImageUrl.isNotBlank()) {
        Dialog(
            onDismissRequest = { showImageDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                ZoomableImage(
                    imageUrl = selectedImageUrl,
                    onDismiss = { showImageDialog = false }
                )
            }
        }
    }

    // Update KYC Dialog
    if (showUpdateKycDialog) {
        UpdateKycDialog(
            data = data,
            viewModel = viewModel,
            onDismiss = { showUpdateKycDialog = false },
            aadharFrontUri = aadharFrontUri,
            aadharBackUri = aadharBackUri,
            panCardUri = panCardUri,
            regFormUri = regFormUri,
            propertyPaperUri = propertyPaperUri,
            aiftidevitUri = aiftidevitUri,
            propertyImgUri = propertyImgUri,
            onPickClick = { field ->
                activeField = field
                showImagePicker = true
            },
            onClearClick = { field ->
                when (field) {
                    "aadharFront" -> { aadharFrontUri = null; aadharFrontBase64 = "" }
                    "aadharBack" -> { aadharBackUri = null; aadharBackBase64 = "" }
                    "panCard" -> { panCardUri = null; panCardBase64 = "" }
                    "regForm" -> { regFormUri = null; regFormBase64 = "" }
                    "propertyPaper" -> { propertyPaperUri = null; propertyPaperBase64 = "" }
                    "aiftidevit" -> { aiftidevitUri = null; aiftidevitBase64 = "" }
                    "propertyImg" -> { propertyImgUri = null; propertyImgBase64 = "" }
                }
            },
            aadharFrontBase64 = aadharFrontBase64,
            aadharBackBase64 = aadharBackBase64,
            panCardBase64 = panCardBase64,
            regFormBase64 = regFormBase64,
            propertyPaperBase64 = propertyPaperBase64,
            aiftidevitBase64 = aiftidevitBase64,
            propertyImgBase64 = propertyImgBase64
        )
    }

    // Image Picker Bottom Sheet
    if (showImagePicker) {
        ImagePicker.ShowImagePicker(
            showBottomSheet = true,
            onDismiss = { showImagePicker = false },
            onImageSelected = { imgRes ->
                imgRes?.let {
                    assignImageResult(it, activeField) { f, u, b ->
                        when (f) {
                            "aadharFront" -> { aadharFrontUri = u; aadharFrontBase64 = b }
                            "aadharBack" -> { aadharBackUri = u; aadharBackBase64 = b }
                            "panCard" -> { panCardUri = u; panCardBase64 = b }
                            "regForm" -> { regFormUri = u; regFormBase64 = b }
                            "propertyPaper" -> { propertyPaperUri = u; propertyPaperBase64 = b }
                            "aiftidevit" -> { aiftidevitUri = u; aiftidevitBase64 = b }
                            "propertyImg" -> { propertyImgUri = u; propertyImgBase64 = b }
                        }
                    }
                }
                showImagePicker = false
            },
            onCameraSelected = {
                showImagePicker = false
                showUpdateKycDialog = false // Close dialog
                onOpenFullCamera { imgRes ->
                    imgRes?.let {
                        assignImageResult(it, activeField) { f, u, b ->
                            when (f) {
                                "aadharFront" -> { aadharFrontUri = u; aadharFrontBase64 = b }
                                "aadharBack" -> { aadharBackUri = u; aadharBackBase64 = b }
                                "panCard" -> { panCardUri = u; panCardBase64 = b }
                                "regForm" -> { regFormUri = u; regFormBase64 = b }
                                "propertyPaper" -> { propertyPaperUri = u; propertyPaperBase64 = b }
                                "aiftidevit" -> { aiftidevitUri = u; aiftidevitBase64 = b }
                                "propertyImg" -> { propertyImgUri = u; propertyImgBase64 = b }
                            }
                        }
                    }
                    showUpdateKycDialog = true // Re-open dialog with image
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun UpdateKycDialog(
    data: PropertyReportModel.NP.ResponseData,
    viewModel: PropertyReportViewModel,
    onDismiss: () -> Unit,
    aadharFrontUri: Uri?,
    aadharBackUri: Uri?,
    panCardUri: Uri?,
    regFormUri: Uri?,
    propertyPaperUri: Uri?,
    aiftidevitUri: Uri?,
    propertyImgUri: Uri?,
    onPickClick: (String) -> Unit,
    onClearClick: (String) -> Unit,
    aadharFrontBase64: String,
    aadharBackBase64: String,
    panCardBase64: String,
    regFormBase64: String,
    propertyPaperBase64: String,
    aiftidevitBase64: String,
    propertyImgBase64: String
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.925f)
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "दस्तावेज़ अपलोड करें (Cust ID: ${data.uniqueId})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "कम से कम एक तस्वीर अपलोड करना अनिवार्य है",
                    fontSize = 13.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold
                )

                HorizontalDivider()

                // 1. आधार (सामने)
                DocumentUploadBox(
                    title = "आधार (सामने)",
                    imageUri = aadharFrontUri,
                    onPickClick = { onPickClick("aadharFront") },
                    onClearClick = { onClearClick("aadharFront") }
                )

                // 2. आधार (पीछे)
                DocumentUploadBox(
                    title = "आधार (पीछे)",
                    imageUri = aadharBackUri,
                    onPickClick = { onPickClick("aadharBack") },
                    onClearClick = { onClearClick("aadharBack") }
                )

                // 3. पैन कार्ड
                DocumentUploadBox(
                    title = "पैन कार्ड",
                    imageUri = panCardUri,
                    onPickClick = { onPickClick("panCard") },
                    onClearClick = { onClearClick("panCard") }
                )

                // 4. रजिस्ट्रेशन फॉर्म
                DocumentUploadBox(
                    title = "रजिस्ट्रेशन फॉर्म",
                    imageUri = regFormUri,
                    onPickClick = { onPickClick("regForm") },
                    onClearClick = { onClearClick("regForm") }
                )

                // 5. संपत्ति कागजात
                DocumentUploadBox(
                    title = "संपत्ति कागजात",
                    imageUri = propertyPaperUri,
                    onPickClick = { onPickClick("propertyPaper") },
                    onClearClick = { onClearClick("propertyPaper") }
                )

                // 6. शपथ पत्र
                DocumentUploadBox(
                    title = "शपथ पत्र",
                    imageUri = aiftidevitUri,
                    onPickClick = { onPickClick("aiftidevit") },
                    onClearClick = { onClearClick("aiftidevit") }
                )

                // 7. संपत्ति फोटो
                DocumentUploadBox(
                    title = "संपत्ति फोटो",
                    imageUri = propertyImgUri,
                    onPickClick = { onPickClick("propertyImg") },
                    onClearClick = { onClearClick("propertyImg") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "रद्द करें", fontWeight = FontWeight.Bold, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            viewModel.updateKyc(
                                context = context,
                                uniqueId = data.uniqueId,
                                aadharFront = aadharFrontBase64,
                                aadharBack = aadharBackBase64,
                                panCard = panCardBase64,
                                regForm = regFormBase64,
                                propertyPaper = propertyPaperBase64,
                                aiftidevit = aiftidevitBase64,
                                image = propertyImgBase64,
                                onSuccess = onDismiss
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00))
                    ) {
                        Text(text = "अपडेट करें", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

private fun assignImageResult(
    imgRes: ImagePicker.ImageResult,
    field: String?,
    onAssign: (String, Uri?, String) -> Unit
) {
    field?.let {
        onAssign(it, imgRes.uri, imgRes.base64 ?: "")
    }
}

@Composable
fun DocumentUploadBox(
    title: String,
    imageUri: Uri?,
    onPickClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (imageUri != null) {
                IconButton(
                    onClick = onClearClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF3E0))
                .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(12.dp))
                .clickable { onPickClick() },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFFF6F00), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudUpload,
                        contentDescription = "Upload",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ZoomableImage(
    imageUrl: String?,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 3f)
        offsetX += panChange.x
        offsetY += panChange.y
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .transformable(state = transformableState)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Zoomable Property Image",
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.FillBounds,
            error = painterResource(id = android.R.drawable.ic_menu_gallery),
            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
        )
    }
}

private fun getPropertyDetails(data: PropertyReportModel.NP.ResponseData): List<Pair<String, String>> {
    val details = mutableListOf<Pair<String, String>>()

    fun addIfValid(label: String, value: String?) {
        if (!value.isNullOrBlank() &&
            value.trim() != "0" &&
            value.trim() != "0.0" &&
            value.trim().lowercase() != "null" &&
            value.trim().lowercase() != "n/a") {
            details.add(label to value.trim())
        }
    }

    addIfValid("विवरण संख्या", data.id)
    addIfValid("प्रॉपर्टी संख्या", data.ulbCode.ifBlank { data.propertyNo })
    addIfValid("Uid संख्या", data.uniqueId)
    addIfValid("क्रम संख्या", data.sequence)
    addIfValid("मुखिया का नाम", data.ownerName)
    addIfValid("पिता/पति का नाम", data.fatherHusbandName)
    addIfValid("मोबाइल नं", data.mobileNo)
    addIfValid("वार्ड का नाम", data.wardName)
    addIfValid("मोहल्ला का नाम", data.mohallaName.ifBlank { data.mohalla })
    addIfValid("संम्पत्ति", data.property)
    addIfValid("संम्पत्ति के प्रकार", data.propertyType)
    addIfValid("भूखण्ड/प्लाट के प्रकार", data.bhukhandPlotType)
    addIfValid("तल संख्या", data.floorNo)
    addIfValid("निर्माण के प्रकार", data.constructionYear)
    addIfValid("सम्पूर्ण क्ष्रेत्र", data.totalArea)
    addIfValid("सम्बन्ध", data.relation)
    addIfValid("सरकरी संम्पत्ति", data.govProperty)
    addIfValid("गैरसरकारी संम्पत्ति", data.nonGovProperty)
    addIfValid("आवासीय क्ष्रेत्र", data.residential)
    addIfValid("व्यवसायिक क्ष्रेत्र", data.commercial)
    addIfValid("रोड चौड़ाई", data.rodeWide)
    addIfValid("खाली क्ष्रेत्र", data.emptyArea)
    addIfValid("पता", data.address)

    return details
}
