package com.example.npassigntech.uiScreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.npassigntech.component.AppTopBar
import com.example.npassigntech.component.NoDataAnimation
import com.example.npassigntech.component.Result
import com.example.npassigntech.model.responseModel.PropertyReportModel
import com.example.npassigntech.sharedPreferences.SharedPreferencesManager
import com.example.npassigntech.viewModel.PropertyReportViewModel
import kotlin.math.roundToInt


@Composable
fun PropertyReport(
    navController: NavHostController,
    viewModel: PropertyReportViewModel = hiltViewModel()
) {
    val userPropertyReportState by viewModel.userPropertyReport.collectAsState()
    val allPropertyReportState by viewModel.allPropertyReport.collectAsState()
    val context = LocalContext.current
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    viewModel.userNo = SharedPreferencesManager.getInstance(context).getValue("UserNumber", "").toString()

    LaunchedEffect(viewModel.userNo) {
        if (viewModel.userNo.isNotEmpty()) {
            viewModel.fetchUserProperties(viewModel.userNo)
        }
    }

    PropertyDetailsScreen(
        userPropertyReportState = userPropertyReportState,
        allPropertyReportState = allPropertyReportState,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        navController = navController,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailsScreen(
    userPropertyReportState: Result<PropertyReportModel>,
    allPropertyReportState: Result<PropertyReportModel>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    navController: NavHostController,
) {
    val totalProperties = when (allPropertyReportState) {
        is Result.Success -> allPropertyReportState.data.nP.responseData.size
        else -> 0
    }

    val entryByUser = when (userPropertyReportState) {
        is Result.Success -> userPropertyReportState.data.nP.responseData.size
        else -> 0
    }

    AppTopBar(
        title = "Property Details",
        onNavigationIconClick = {
            navController.popBackStack()
        },
        rememberNavController = navController,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Row for the two cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Total Properties Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                            .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Properties",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = totalProperties.toString(),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Entry by User Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                            .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Entry by User",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = entryByUser.toString(),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // PullToRefreshBox content
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (userPropertyReportState) {
                        is Result.Loading -> {
                            if (isRefreshing) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 5.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(5) {
                                        ShimmerPropertyItem()
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        is Result.Success -> {
                            val data = userPropertyReportState.data
                            val responseDataList = data.nP.responseData
                            if (responseDataList.isEmpty()) {
                                NoDataAnimation()
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 5.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(responseDataList) { responseData ->
                                        PropertyDetailCard(responseData)
                                    }
                                }
                            }
                        }

                        is Result.Error -> {
                            val error = userPropertyReportState.toString()
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Error: $error",
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Result.Idle -> {
                            NoDataAnimation()
                        }
                    }
                }
            }
        }
    )
}
@Composable
fun PropertyDetailCard(data: PropertyReportModel.NP.ResponseData) {
    // State to control dialog visibility
    var showImageDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "संम्पत्ति विवरण",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.background,
                    textAlign = TextAlign.Center
                )
            }
            getPropertyDetails(data).forEach { detail ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${detail.first}:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = detail.second,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data.imagesUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Property Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { showImageDialog = true },
                contentScale = ContentScale.Crop,
                error = painterResource(id = android.R.drawable.ic_menu_gallery),
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
            )

            // Dialog for zoomable image
            if (showImageDialog) {
                Dialog(
                    onDismissRequest = { showImageDialog = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        ZoomableImage(
                            imageUrl = data.imagesUrl,
                            onDismiss = { showImageDialog = false }
                        )
                    }
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

    // Gesture detector for pinch and pan
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 3f) // Limit zoom between 0.5x and 3x
        offsetX += panChange.x
        offsetY += panChange.y
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Maintain aspect ratio for image
            .transformable(state = transformableState)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() } // Click outside image to dismiss
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

@Composable
fun ShimmerPropertyItem() {
    val shimmerColor = Color(0xFFB8B8B8)
    val shimmerColorDark = Color(0xFFAAAAAA)
    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            )
        )
    )

    val brush = Brush.linearGradient(
        colors = listOf(shimmerColor, shimmerColorDark, shimmerColor),
        start = Offset(translateAnim.value - 200f, 0f),
        end = Offset(translateAnim.value, 0f)
    )

    val density = LocalDensity.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { 25.sp.toDp() })
                    .background(brush)
            )

            repeat(25) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(with(density) { 14.sp.toDp() })
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(with(density) { 14.sp.toDp() })
                            .background(brush)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(brush)
            )
        }
    }
}

private fun getPropertyDetails(data: PropertyReportModel.NP.ResponseData): List<Pair<String, String>> {
    return listOf(
        "विवरण संख्या" to data.id.toString(),
        "क्रम संख्या" to data.sequence,
        "संम्पत्ति संख्या" to data.propertyNo,
        "मुखिया का नाम" to data.ownerName,
        "पिता/पति का नाम" to data.fatherHusbandName,
        "मोबाइल नं" to data.mobileNo,
        "वार्ड का नाम" to data.wardName,
        "मोहल्ला का नाम" to data.mohallaName,
        "संम्पत्ति" to data.property,
        "संम्पत्ति के प्रकार" to data.propertyType,
        "भूखण्ड/प्लाट के प्रकार" to data.bhukhandPlotType,
        "निर्माण के प्रकार" to data.constructionType,
        "निर्माण वर्ष" to data.constructionYear,
        "तल संख्या" to data.floorNo,
        "सम्पूर्ण क्ष्रेत्र" to data.totalArea,
        "लिंग" to data.gender,
        "सम्बन्ध" to data.relation,
        "सरकरी/गैरसरकारी" to data.govNonGov,
        "सरकरी संम्पत्ति" to data.govProperty,
        "गैरसरकारी संम्पत्ति" to data.nonGovProperty,
        "आवासीय क्ष्रेत्र" to data.residential,
        "व्यवसायिक क्ष्रेत्र" to data.commercial,
        "धर्म" to data.religious,
        "नहर/तालाब/कुआं/पोखरा" to data.naharTalab,
        "रोड चौड़ाई" to data.rodeWide,
        "खाली क्ष्रेत्र" to data.emptyArea,
        "पता" to data.address
    )
}