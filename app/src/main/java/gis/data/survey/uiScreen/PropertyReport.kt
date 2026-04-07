package gis.data.survey.uiScreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import gis.data.survey.component.AppTopBar
import gis.data.survey.component.NoDataAnimation
import gis.data.survey.model.responseModel.PropertyReportModel
import gis.data.survey.sharedPreferences.SharedPreferencesManager
import gis.data.survey.viewModel.PropertyReportViewModel
import kotlin.math.roundToInt
import gis.data.survey.component.Result
import kotlinx.coroutines.launch

@Composable
fun PropertyReport(
    navController: NavHostController,
    viewModel: PropertyReportViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    val userPropertyReportState by viewModel.userPropertyReport.collectAsState()
    val allPropertyReportState by viewModel.allPropertyReport.collectAsState()
    val context = LocalContext.current
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    viewModel.userNo = SharedPreferencesManager.getInstance(context).getValue("UserNumber", "").toString()

    LaunchedEffect(viewModel.userNo) {
        if (viewModel.userNo.isNotEmpty()) {
            viewModel.fetchUserProperties(viewModel.userNo)
//            viewModel.fetchUserProperties("")
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PropertyDetailsScreen(
//    userPropertyReportState: Result<PropertyReportModel>,
//    allPropertyReportState: Result<PropertyReportModel>,
//    isRefreshing: Boolean,
//    onRefresh: () -> Unit,
//    navController: NavHostController,
//) {
//
//    val totalProperties = when (allPropertyReportState) {
//        is Result.Success -> allPropertyReportState.data.nP.responseData.size
//        else -> 0
//    }
//
//    val entryByUser = when (userPropertyReportState) {
//        is Result.Success -> userPropertyReportState.data.nP.responseData.size
//        else -> 0
//    }
//
//    AppTopBar(
//        title = "Property Details",
//        onNavigationIconClick = { navController.popBackStack() },
//        rememberNavController = navController,
//        content = { innerPadding ->
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding)
//            ) {
//
//                // 🔷 TOP CARDS
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(5.dp)
//                ) {
//
//                    Card(
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(end = 4.dp)
//                            .border(1.dp, Color.Gray)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(8.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text("Total Properties", fontWeight = FontWeight.Bold)
//                            Text(totalProperties.toString())
//                        }
//                    }
//
//                    Card(
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(start = 4.dp)
//                            .border(1.dp, Color.Gray)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(8.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text("Entry by User", fontWeight = FontWeight.Bold)
//                            Text(entryByUser.toString())
//                        }
//                    }
//                }
//
//                // 🔄 PULL TO REFRESH
//                PullToRefreshBox(
//                    isRefreshing = isRefreshing,
//                    onRefresh = onRefresh,
//                    modifier = Modifier.fillMaxSize()
//                ) {
//
//                    when (userPropertyReportState) {
//
//                        is Result.Loading -> {
//                            Box(
//                                Modifier.fillMaxSize(),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                CircularProgressIndicator()
//                            }
//                        }
//
//                        is Result.Success -> {
//
//                            val list = userPropertyReportState.data.nP.responseData
//
//                            if (list.isEmpty()) {
//                                NoDataAnimation()
//                            } else {
//
//                                val listState = rememberLazyListState()
//                                val coroutineScope = rememberCoroutineScope()
//
//                                Box(modifier = Modifier.fillMaxSize()) {
//
//                                    // 🔹 PROPERTY LIST
//                                    LazyColumn(
//                                        state = listState,
//                                        modifier = Modifier
//                                            .fillMaxSize()
//                                            .padding(horizontal = 5.dp),
//                                        verticalArrangement = Arrangement.spacedBy(10.dp)
//                                    ) {
//                                        items(list.size) { index ->
//                                            PropertyDetailCard(list[index])
//                                        }
//                                    }
//
//                                    // 🔥 DRAGGABLE SCROLLBAR
//                                    val totalItems = list.size
//                                    val visibleIndex =
//                                        listState.firstVisibleItemIndex
//
//                                    Column(
//                                        modifier = Modifier
//                                            .align(Alignment.CenterEnd)
//                                            .padding(end = 6.dp),
//                                        horizontalAlignment = Alignment.CenterHorizontally
//                                    ) {
//
//                                        // 🔥 FLOATING ID
//                                        Box(
//                                            modifier = Modifier
//                                                .background(
//                                                    Color.Black.copy(alpha = 0.7f),
//                                                    RoundedCornerShape(6.dp)
//                                                )
//                                                .padding(
//                                                    horizontal = 8.dp,
//                                                    vertical = 4.dp
//                                                )
//                                        ) {
//                                            Text(
//                                                text = "ID: ${list[visibleIndex].id}",
//                                                color = Color.White,
//                                                fontSize = 12.sp
//                                            )
//                                        }
//
//                                        Spacer(Modifier.height(6.dp))
//
//                                        val trackHeight = 300f
//
//                                        Box(
//                                            modifier = Modifier
//                                                .width(20.dp) // touch area
//                                                .height(trackHeight.dp)
//                                                .pointerInput(Unit) {
//                                                    detectDragGestures { change, _ ->
//
//                                                        val proportion =
//                                                            (change.position.y / size.height)
//                                                                .coerceIn(0f, 1f)
//
//                                                        val targetIndex =
//                                                            (proportion * (totalItems - 1)).toInt()
//
//                                                        coroutineScope.launch {
//                                                            listState.scrollToItem(
//                                                                targetIndex
//                                                            )
//                                                        }
//                                                    }
//                                                },
//                                            contentAlignment = Alignment.TopCenter
//                                        ) {
//
//                                            // Track
//                                            Box(
//                                                modifier = Modifier
//                                                    .width(4.dp)
//                                                    .fillMaxHeight()
//                                                    .background(
//                                                        Color.LightGray,
//                                                        RoundedCornerShape(2.dp)
//                                                    )
//                                            )
//
//                                            // Thumb
//                                            val proportion =
//                                                visibleIndex.toFloat() /
//                                                        (totalItems - 1).coerceAtLeast(1)
//
//                                            Box(
//                                                modifier = Modifier
//                                                    .offset {
//                                                        IntOffset(
//                                                            0,
//                                                            (proportion * (trackHeight - 40)).toInt()
//                                                        )
//                                                    }
//                                                    .width(8.dp)
//                                                    .height(40.dp)
//                                                    .background(
//                                                        MaterialTheme.colorScheme.primary,
//                                                        RoundedCornerShape(4.dp)
//                                                    )
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        is Result.Error -> {
//                            Box(
//                                Modifier.fillMaxSize(),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Text(
//                                    "Error loading data",
//                                    color = MaterialTheme.colorScheme.error
//                                )
//                            }
//                        }
//
//                        Result.Idle -> {
//                            NoDataAnimation()
//                        }
//                    }
//                }
//            }
//        }
//    )
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailsScreen(
    userPropertyReportState: Result<PropertyReportModel>,
    allPropertyReportState: Result<PropertyReportModel>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    navController: NavHostController,
) {
    // Dropdown States
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
                // --- Dropdown Filters Section ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Ward Dropdown
                    if (userPropertyReportState is Result.Success) {
                        val rawList = userPropertyReportState.data.nP.responseData
                        val wards = listOf("All Wards") + rawList.map { it.wardName }.distinct().sorted()

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
                                            selectedMohalla = "All Mohallas" // Reset mohalla on ward change
                                            isWardExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // 2. Mohalla Dropdown
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

                            // 🎯 FILTER LOGIC
                            val filteredList = remember(selectedWard, selectedMohalla, rawList) {
                                rawList.filter { item ->
                                    val wardMatch = selectedWard == "All Wards" || item.wardName == selectedWard
                                    val mohallaMatch = selectedMohalla == "All Mohallas" || item.mohallaName == selectedMohalla
                                    wardMatch && mohallaMatch
                                }
                            }

                            Column {
                                // 🔢 Result Count
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
                                        modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        items(filteredList.size) { index ->
                                            PropertyDetailCard(filteredList[index])
                                        }
                                    }
                                }
                            }
                        }
                        is Result.Loading -> { /* CircularProgressIndicator */ }
                        is Result.Error -> { /* Error UI */ }
                        else -> { NoDataAnimation() }
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

                // Check if the current row is one of the target fields
                val isTarget = detail.first == "प्रॉपर्टी संख्या" || detail.first == "Uid संख्या"

                // Set colors and weight based on the condition
                val targetColor = if (isTarget) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                val labelColor = if (isTarget) Color(0xFF1B5E20) else MaterialTheme.colorScheme.primary
                val fontWeight = if (isTarget) FontWeight.ExtraBold else FontWeight.Normal

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${detail.first}:",
                        fontWeight = if (isTarget) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 14.sp,
                        color = labelColor,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = detail.second,
                        fontSize = 15.sp, // थोड़ा सा साइज बढ़ा दिया है ताकि और साफ दिखे
                        fontWeight = fontWeight,
                        color = targetColor,
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
        "प्रॉपर्टी संख्या" to data.ulbCode,
        "Uid संख्या" to data.uniqueId,
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
        "निर्माण के प्रकार" to data.primaryConstructionType,
        "उपनिर्माण वर्ष" to data.constructionType,
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