package com.example.npmlbd.uiScreen


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.npmlbd.component.Result
import com.example.npmlbd.model.responseModel.VerifyOtpRes
import com.example.npmlbd.navigation.Screen
import com.example.npmlbd.sharedPreferences.SharedPreferencesManager
import com.example.npmlbd.viewModel.VerifyOtp
import kotlin.random.Random

@Composable
fun OtpScreen(
    phoneNumber: String,
    setOnOtpReceived: ((String) -> Unit) -> Unit,
    onBack: () -> Unit,
    navController: NavHostController,
    viewModel: VerifyOtp = hiltViewModel()
) {
    Log.d("otp", "OtpScreen: "+setOnOtpReceived)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    //
    val otpDigits = remember { List(6) { mutableStateOf(TextFieldValue("")) } }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val isFocused = remember { List(6) { mutableStateOf(false) } }

    val verifyOtp by viewModel.verifyOtp.collectAsState()

    // Auto-fill OTP and trigger verification
    setOnOtpReceived { otp ->
        otp.forEachIndexed { index, char ->
            if (index < 6) {
                otpDigits[index].value = TextFieldValue(char.toString())
            }
        }
        coroutineScope.launch {
            Toast.makeText(context, "OTP Auto-filled: $otp", Toast.LENGTH_SHORT).show()
            if (otp.length == 6) {
                viewModel.fetchVerifyOtp(phoneNumber, otp)
                Toast.makeText(context, "Verifying OTP: $otp", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Animated Background
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundAnimation")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetAnimation"
    )

    val gradientColors = listOf(
        Color(0xFF0F172A),
        Color(0xFF1E3A8A),
        Color(0xFF0F172A)
    )

    val animatedBrush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(0f, animatedOffset * 1000f),
        end = Offset(1000f, (1f - animatedOffset) * 1000f)
    )

    // Monitor OTP completion for manual input
    LaunchedEffect(otpDigits) {
        val isOtpComplete = otpDigits.all { it.value.text.length == 1 }
        if (isOtpComplete) {
            val otp = otpDigits.joinToString("") { it.value.text }
            viewModel.fetchVerifyOtp(phoneNumber, otp)
            Toast.makeText(context, "Verifying OTP: $otp", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(verifyOtp) {
        when (verifyOtp) {
            is Result.Success -> {
                val res = (verifyOtp as Result.Success<VerifyOtpRes>).data
                SharedPreferencesManager.getInstance(context).setValue("loginState",res.nP.responseMessage)

                if (res.nP.responseMessage=="Login Successful"){
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.OtpLogin.route) { inclusive = true }
                    }
                }

            }
            is com.example.npmlbd.component.Result.Error -> {
                Toast.makeText(
                    context,
                    "Error: ${(verifyOtp as com.example.npmlbd.component.Result.Error).errorMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBrush)
    ) {
        StarryBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1200)),
                exit = fadeOut(animationSpec = tween(1200))
            ) {
                Text(
                    text = "Enter OTP",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 28.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Text(
                text = "Sent to +91$phoneNumber",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .background(Color.White),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    otpDigits.forEachIndexed { index, digitState ->
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(
                                    width = 2.dp,
                                    color = if (isFocused[index].value) Color(0xFF3B82F6) else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(
                                value = digitState.value,
                                onValueChange = { newValue ->
                                    if (newValue.text.length <= 1 && (newValue.text.isEmpty() || newValue.text.all { it.isDigit() })) {
                                        digitState.value = newValue
                                        if (newValue.text.isNotEmpty() && index < 5) {
                                            focusRequesters[index + 1].requestFocus()
                                        } else if (newValue.text.isEmpty() && index > 0) {
                                            focusRequesters[index - 1].requestFocus()
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .focusRequester(focusRequesters[index])
                                    .onFocusChanged { isFocused[index].value = it.isFocused }
                                    .onKeyEvent { event ->
                                        if (event.key == Key.Backspace && digitState.value.text.isEmpty() && index > 0) {
                                            focusRequesters[index - 1].requestFocus()
                                            true
                                        } else false
                                    },
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    color = Color.Black
                                ),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        innerTextField()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val isOtpComplete = otpDigits.all { it.value.text.length == 1 }
            AnimatedVisibility(
                visible = isOtpComplete,
                enter = scaleIn(animationSpec = tween(600)),
                exit = scaleOut(animationSpec = tween(600))
            ) {
                Button(
                    onClick = {
                        val otp = otpDigits.joinToString("") { it.value.text }
                        viewModel.fetchVerifyOtp(phoneNumber, otp)
                        Toast.makeText(context, "Verifying OTP: $otp", Toast.LENGTH_SHORT).show()
                    },
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

                    if (verifyOtp is Result.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Verify",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { onBack() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Back to Mobile Number",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun StarryBackground() {
    val stars = remember { generateStars(100) }
    val infiniteTransition = rememberInfiniteTransition(label = "starryAnimation")
    val starAlphas = stars.map { star ->
        infiniteTransition.animateFloat(
            initialValue = star.minAlpha,
            targetValue = star.maxAlpha,
            animationSpec = infiniteRepeatable(
                animation = tween(star.twinkleDuration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "starAlphaAnimation_${star.x}_${star.y}"
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEachIndexed { index, star ->
            drawCircle(
                color = Color.White.copy(alpha = starAlphas[index].value),
                radius = star.size,
                center = Offset(star.x, star.y)
            )
        }
    }
}

data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val minAlpha: Float,
    val maxAlpha: Float,
    val twinkleDuration: Int
)

fun generateStars(count: Int): List<Star> {
    return List(count) {
        Star(
            x = Random.nextFloat() * 2000f,
            y = Random.nextFloat() * 3000f,
            size = Random.nextFloat() * 2f + 1f,
            minAlpha = 0.2f + Random.nextFloat() * 0.3f,
            maxAlpha = 0.8f + Random.nextFloat() * 0.2f,
            twinkleDuration = 1000 + Random.nextInt(2000)
        )
    }
}

