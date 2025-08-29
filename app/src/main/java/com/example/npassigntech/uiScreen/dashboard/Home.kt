package com.example.npassigntech.uiScreen.dashboard

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MapsHomeWork
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout . *
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.npassigntech.model.responseModel.HomeItem
import com.example.npassigntech.model.responseModel.SliderItem
import com.example.npassigntech.navigation.Screen
import kotlinx.coroutines.delay
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.Water
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.npassigntech.R

@Composable
fun Home(navController: NavHostController) {


    val sliderItems = listOf(
        SliderItem(id = "1", R.drawable.image1),
        SliderItem(id = "2", R.drawable.sliderimg2),
        SliderItem(id = "3", R.drawable.sliderimg3)
    )

    val gridItemsForRegistration = listOf(
        HomeItem("Property Registration", Icons.Filled.MapsHomeWork, Screen.PropertyRegistration),
        HomeItem("Property Details", Icons.AutoMirrored.Filled.List, Screen.PropertyReport),
        )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .padding(bottom = 40.dp)
            .onSizeChanged { size ->
                println("LazyColumn size: ${size.width}x${size.height}")
            },
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        item {
            MarqueeText(
                text = "Welcome to the Nagar Panchayat Technical Helpline Number: +91 9670896705",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                speed = 80
            )
        }
        item {
            ImageSlider(
                items = sliderItems,
                onImageClick = { item ->
                    println("Clicked on image: ${item.id}")
                }
            )
        }
        item {
            Text(
                text = "Survey Registration",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        item {
            HomeGrid(
                items = gridItemsForRegistration,
                onItemClick = { item ->
                    item.route?.let { route ->
                        navController.navigate(route.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ImageSlider(
    items: List<SliderItem>,
    modifier: Modifier = Modifier,
    autoScroll: Boolean = true,
    autoScrollDelay: Long = 3000L,
    onImageClick: (SliderItem) -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text("No images available", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { items.size })

    if (autoScroll) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(autoScrollDelay)
                val nextPage = (pagerState.currentPage + 1) % items.size
                try {
                    pagerState.animateScrollToPage(nextPage)
                } catch (e: Exception) {
                    // Handle cancellation gracefully
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .clipToBounds(),
            contentPadding = PaddingValues(0.dp),
            pageSpacing = 0.dp
        ) { page ->
            val item = items[page]
            AsyncImage(
                model = item.drawableResId,
                contentDescription = "Slider Image ${item.id}",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onImageClick(item) }
            )
        }

        if (items.size > 1) {
            DotsIndicator(
                totalDots = items.size,
                selectedIndex = pagerState.currentPage,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == selectedIndex) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == selectedIndex) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
fun HomeGrid(
    items: List<HomeItem>,
    onItemClick: (HomeItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),

        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            HomeGridItem(item = item, onClick = { onItemClick(item) })
        }
    }
}

@Composable
fun HomeGridItem(item: HomeItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    speed: Int = 50
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier.clipToBounds()
    ) {
        Text(
            text = text,
            style = textStyle,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.horizontalScroll(scrollState, enabled = false)
        )
    }

    LaunchedEffect(text) {
        while (true) {
            val maxScroll = scrollState.maxValue
            if (maxScroll > 0) {
                scrollState.animateScrollTo(
                    maxScroll,
                    animationSpec = tween(
                        durationMillis = (maxScroll * 1000 / speed).toInt(),
                        easing = LinearEasing
                    )
                )
                delay(1000)
                scrollState.scrollTo(0)
                delay(500)
            } else {
                delay(1000)
            }
        }
    }
}