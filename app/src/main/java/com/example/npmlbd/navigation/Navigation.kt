package com.example.npmlbd.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.npmlbd.uiScreen.Login
import com.example.npmlbd.uiScreen.Main
import com.example.npmlbd.uiScreen.OtpLogin
import com.example.npmlbd.uiScreen.OtpScreen
import com.example.npmlbd.uiScreen.PropertyRegistration
import com.example.npmlbd.uiScreen.PropertyRegistrationPreviewScreen
import com.example.npmlbd.uiScreen.Splash
import com.example.npmlbd.uiScreen.PropertyReport
import com.example.npmlbd.uiScreen.drawer.About
import com.example.npmlbd.uiScreen.drawer.Profile
import com.example.npmlbd.uiScreen.drawer.Settings

//@RequiresApi(Build.VERSION_CODES.R)
//@Composable
//fun AppNavigation(rememberNavController: NavHostController) {
//    NavHost(
//        navController = rememberNavController,
//        startDestination = Screen.OtpLogin.route,
//        modifier = Modifier.fillMaxSize(),
//        enterTransition = {
//            slideInHorizontally(
//                initialOffsetX = { fullWidth -> fullWidth },
//                animationSpec = tween(300)
//            ) + fadeIn(animationSpec = tween(300))
//        },
//        exitTransition = {
//            slideOutHorizontally(
//                targetOffsetX = { fullWidth -> -fullWidth },
//                animationSpec = tween(300)
//            ) + fadeOut(animationSpec = tween(300))
//        },
//        popEnterTransition = {
//            slideInHorizontally(
//                initialOffsetX = { fullWidth -> -fullWidth },
//                animationSpec = tween(300)
//            ) + fadeIn(animationSpec = tween(300))
//        },
//        popExitTransition = {
//            slideOutHorizontally(
//                targetOffsetX = { fullWidth -> fullWidth },
//                animationSpec = tween(300)
//            ) + fadeOut(animationSpec = tween(300))
//        },
//
//        ) {
//        composable(Screen.Splash.route) { Splash(rememberNavController) }
//        composable(Screen.Login.route) { Login(rememberNavController) }
//        composable(Screen.Dashboard.route) { Dashboard(rememberNavController) }
//        composable(Screen.PropertyRegistration.route) { PropertyRegistration(rememberNavController) }
//        composable(Screen.PropertyReport.route) { PropertyReport(rememberNavController) }
//        composable(Screen.Settings.route) { Settings(rememberNavController) }
//        composable(Screen.About.route) { About(rememberNavController) }
//        composable(Screen.PropertyRegistrationPreview.route) { PropertyRegistrationPreviewScreen(rememberNavController) }
//        composable(Screen.OtpLogin.route) { Otp("6388571644",) }
//
//    }
//}


@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun AppNavigation(setOnOtpReceived: ((String) -> Unit) -> Unit) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(700)
            ) + fadeIn(animationSpec = tween(700))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(700)
            ) + fadeOut(animationSpec = tween(700))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(700)
            ) + fadeIn(animationSpec = tween(700))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(700)
            ) + fadeOut(animationSpec = tween(700))
        }
    ) {
        composable(Screen.Splash.route) { Splash(navController) }
        composable(Screen.Login.route) { Login(navController) }
        composable(Screen.Main.route) { Main(navController) }
        composable(Screen.PropertyRegistration.route) { PropertyRegistration(navController) }
        composable(Screen.PropertyReport.route) { PropertyReport(navController) }
        composable(Screen.Settings.route) { Settings(navController) }
        composable(Screen.About.route) { About(navController) }
        composable(Screen.Profile.route) { Profile(navController) }
        composable(Screen.PropertyRegistrationPreview.route) { PropertyRegistrationPreviewScreen(navController) }
        composable(Screen.MobileLogin.route) {
            OtpLogin(
                onNavigateToOtp = { phoneNumber ->
                    navController.navigate("otp_login/$phoneNumber"){
                        popUpTo(Screen.MobileLogin.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Screen.OtpLogin.route) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            OtpScreen(
                phoneNumber = phoneNumber,
                setOnOtpReceived = setOnOtpReceived,
                onBack = { navController.popBackStack() },
                navController

            )
        }
    }
}
