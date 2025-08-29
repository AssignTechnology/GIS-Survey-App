package com.example.npassigntech.uiScreen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.npassigntech.component.Result
import com.example.npassigntech.sharedPreferences.SharedPreferencesManager
import com.example.npassigntech.viewModel.OtpLoginViewModel

@Composable
fun OtpLogin(onNavigateToOtp: (String) -> Unit, viewModel: OtpLoginViewModel = hiltViewModel()) {
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var isButtonEnabled by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var countryCode by remember { mutableStateOf("+91") }
    val sendOtp by viewModel.sendOtp.collectAsState(initial = null)
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    // Validate phone number
    LaunchedEffect(phoneNumber.text) {
        isButtonEnabled = phoneNumber.text.length == 10 && phoneNumber.text.all { it.isDigit() }
        showError = phoneNumber.text.isNotEmpty() && !isButtonEnabled
    }

    // Handle API response
    LaunchedEffect(sendOtp) {
        sendOtp?.let { result ->
            when (result) {
                is Result.Success -> {
                    val data = result.data
                    if (data.nP.responseCode == "200") {
                        onNavigateToOtp(phoneNumber.text)
                        SharedPreferencesManager.getInstance(context).setValue("UserNumber",phoneNumber.text).toString()
                        phoneNumber = TextFieldValue("")

                    } else {
                        Toast.makeText(context, data.nP.responseMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                is Result.Error -> {
                    Toast.makeText(context, "Error: ${result.errorMessage}", Toast.LENGTH_LONG).show()
                }
                else -> {
                }
            }
        }
    }

    // Animated Background Gradient
    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val gradientColors = listOf(
        Color(0xFF6B7280),
        Color(0xFF3B82F6),
        Color(0xFF1E3A8A),
        Color(0xFF8B5CF6),
        Color(0xFF6B7280)
    )

    val animatedBrush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(0f, animatedOffset * 1000f),
        end = Offset(1000f, (1f - animatedOffset) * 1000f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Text
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1200)),
                exit = fadeOut(animationSpec = tween(1200))
            ) {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 28.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Subtitle
            Text(
                text = "Enter your mobile number to continue",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Card for input fields
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .background(Color.White),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = countryCode,
                            onValueChange = { countryCode = it },
                            label = { Text("Code") },
                            modifier = Modifier
                                .weight(0.3f)
                                .padding(end = 8.dp),
                            readOnly = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { if (it.text.length <= 10) phoneNumber = it },
                            label = { Text("Mobile Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            isError = showError,
                            modifier = Modifier.weight(0.7f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                    }
                    AnimatedVisibility(
                        visible = showError,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        Text(
                            text = "Please enter a valid 10-digit number",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            AnimatedVisibility(
                visible = isButtonEnabled,
                enter = scaleIn(animationSpec = tween(600)),
                exit = scaleOut(animationSpec = tween(600))
            ) {
                Button(
                    onClick = { viewModel.fetchSendOtp(phoneNumber.text) },
                    enabled = isButtonEnabled && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6),
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Send OTP",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}