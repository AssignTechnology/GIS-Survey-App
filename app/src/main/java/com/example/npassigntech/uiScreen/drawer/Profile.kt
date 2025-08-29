package com.example.npassigntech.uiScreen.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.npassigntech.component.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(navController: NavHostController) {
    AppTopBar(
        title = "Profile",
        onNavigationIconClick = {
            navController.popBackStack()
        },
        rememberNavController = navController,
        content = {
            ProfileScreen(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(paddingValues: PaddingValues) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isSmallScreen = screenWidth < 600.dp // For compact devices
    val avatarSize = if (isSmallScreen) 100.dp else 120.dp
    val buttonWidthFraction = if (isSmallScreen) 0.6f else 0.5f

    var name by remember { mutableStateOf("राम श्याम") }
    var email by remember { mutableStateOf("ram.shyam@example.com") }
    var mobile by remember { mutableStateOf("9876543210") }
    var isEditing by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = if (isSmallScreen) 16.dp else 24.dp, vertical = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // User Avatar
            Image(
                painter = rememberAsyncImagePainter("https://example.com/avatar.jpg"),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = if (isSmallScreen) 24.sp else 28.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            // User Email
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = if (isSmallScreen) 16.sp else 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            // User Mobile
            Text(
                text = mobile,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = if (isSmallScreen) 16.sp else 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Edit/Save Button
            Button(
                onClick = { isEditing = !isEditing },
                modifier = Modifier
                    .fillMaxWidth(buttonWidthFraction)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.ArrowBack else Icons.Default.Edit,
                    contentDescription = if (isEditing) "Save" else "Edit"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Save" else "Edit Profile",
                    fontSize = if (isSmallScreen) 16.sp else 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Editable Fields with Animation
            AnimatedVisibility(
                visible = isEditing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isSmallScreen) 8.dp else 16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { newValue ->
                            if (validateName(newValue) == null) {
                                name = newValue
                            }
                        },
                        label = { Text("Name (नाम)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        isError = validateName(name) != null,
                        supportingText = {
                            validateName(name)?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = if (isSmallScreen) 12.sp else 14.sp
                                )
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = if (isSmallScreen) 14.sp else 16.sp
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email (ईमेल)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = !email.contains("@") || !email.contains("."),
                        supportingText = {
                            if (!email.contains("@") || !email.contains(".")) {
                                Text(
                                    text = "Please enter a valid email",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = if (isSmallScreen) 12.sp else 14.sp
                                )
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = if (isSmallScreen) 14.sp else 16.sp
                        )
                    )

                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { newValue ->
                            if (validateMobile(newValue) == null) {
                                mobile = newValue
                            }
                        },
                        label = { Text("Mobile (मोबाइल)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = validateMobile(mobile) != null,
                        supportingText = {
                            validateMobile(mobile)?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = if (isSmallScreen) 12.sp else 14.sp
                                )
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = if (isSmallScreen) 14.sp else 16.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Info (Non-editable)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Additional Information",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = if (isSmallScreen) 18.sp else 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Location: दिल्ली, भारत",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = if (isSmallScreen) 14.sp else 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Joined: August 2025",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = if (isSmallScreen) 14.sp else 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Name validation function
private fun validateName(value: String): String? {
    return when {
        value.isBlank() -> "नाम आवश्यक है"
        !value.matches(Regex("^[\\u0900-\\u097F\\s]+$")) -> "नाम में केवल हिंदी अक्षर और रिक्त स्थान हो सकते हैं"
        else -> null
    }
}

// Mobile number validation function
private fun validateMobile(value: String): String? {
    return when {
        value.isBlank() -> "मोबाइल नंबर आवश्यक है"
        !value.matches(Regex("^[0-9]{10}$")) -> "मोबाइल नंबर 10 अंकों का होना चाहिए"
        else -> null
    }
}