package com.example.npassigntech.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.npassigntech.navigation.Screen
import com.example.npassigntech.sharedPreferences.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun AppDrawer(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavHostController
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Colorful gradient animation for header and card backgrounds
    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            )
        )
    )
    val colorfulBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF000000), // Deep Purple
            Color(0xFF03DAC5), // Cyan
            Color(0xFF1B5E20), // Pink
            Color(0xFF201136)  // Loop back to Purple
        ),
        start = Offset(translateAnim.value - 200f, 0f),
        end = Offset(translateAnim.value, 0f)
    )
    // Subtle gradient for cards
    val cardBrush = Brush.linearGradient(
        colors = listOf(

            Color(0xFFE1BEE7), // Light Purple
            Color(0xFFB2EBF2), // Light Cyan
            Color(0xFFF8BBD0), // Light Pink
            Color(0xFFE1BEE7)  // Loop back
        ),
        start = Offset(translateAnim.value - 200f, 0f),
        end = Offset(translateAnim.value, 0f)
    )

    // Star blinking and scaling animations
    val starAlpha = transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )
    val starScale = transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Text scale animation
    val textScale = transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    ModalDrawerSheet(
        modifier = Modifier.background(brush = cardBrush) // Apply gradient to drawer background
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = colorfulBrush)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Draw multiple blinking stars with varied properties
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val starColors = listOf(Color.Yellow, Color.Cyan, Color.Magenta, Color.White, Color(0xFFFF4081))
                        val stars = List(8) { // Increased to 8 stars
                            Triple(
                                Offset(
                                    x = Random.nextFloat() * size.width,
                                    y = Random.nextFloat() * size.height
                                ),
                                starColors[Random.nextInt(starColors.size)],
                                Random.nextFloat() * 6f + 4f // Sizes between 4f and 10f
                            )
                        }
                        stars.forEach { (center, color, baseSize) ->
                            val starSize = baseSize * starScale.value
                            drawPath(
                                path = Path().apply {
                                    moveTo(center.x, center.y - starSize)
                                    lineTo(center.x + starSize * 0.3f, center.y - starSize * 0.3f)
                                    lineTo(center.x + starSize, center.y)
                                    lineTo(center.x + starSize * 0.3f, center.y + starSize * 0.3f)
                                    lineTo(center.x, center.y + starSize)
                                    lineTo(center.x - starSize * 0.3f, center.y + starSize * 0.3f)
                                    lineTo(center.x - starSize, center.y)
                                    lineTo(center.x - starSize * 0.3f, center.y - starSize * 0.3f)
                                    close()
                                },
                                color = color,
                                alpha = starAlpha.value
                            )
                        }
                    }

                    Text(
                        text = "Nagar Panchayat",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.scale(textScale.value)
                    )
                }
            }
            // Header with colorful animated background and multiple stars


            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Card {
                Text(
                    text = "Section 1",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    onClick = {
//                        scope.launch {
//                            drawerState.close()
//                            navController.navigate(Screen.Profile.route)
//                        }
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Card {
                Text(
                    text = "Section 2",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },

                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.Settings.route)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = false,
                    icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.About.route)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            showDialog = true
                        }
                    }
                )
            }
            Spacer(Modifier.height(160.dp))

            val appVersion = "1.0.0"
            // Display app version at the bottom

            Card {
                Text(
                    text = "Version $appVersion",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Blue,
                    textAlign = TextAlign.Center
                )
            }

        }

        if (showDialog) {
            DialogAlert(
                dialogTitle = "Logout",
                dialogText = "Are you sure you want to logout?",
                icon = Icons.Default.Warning,
                onDismissRequest = {
                    showDialog = false
                },
                onConfirmation = {
                    SharedPreferencesManager.getInstance(context).removeKey("loginState")
                    SharedPreferencesManager.getInstance(context).removeKey("UserNumber")
                    SharedPreferencesManager.getInstance(context).clear()
                    navController.navigate(Screen.MobileLogin.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                    showDialog = false
                }
            )
        }
    }
}
