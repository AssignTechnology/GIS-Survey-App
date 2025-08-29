package com.example.npassigntech.navigation

import kotlinx.serialization.Serializable

sealed class Screen(val route: String) {
    @Serializable
    object Splash : Screen("splash")

    @Serializable
    object Login : Screen("login")

    @Serializable
    object Main : Screen("main")

    @Serializable
    object Home : Screen("home")

    @Serializable
    object Services : Screen("services")

    @Serializable
    object PropertyRegistration : Screen("propertyRegistration")

    @Serializable
    object PropertyReport : Screen("propertyReport")

    @Serializable
    object Settings : Screen("settings")

    @Serializable
    object About : Screen("about")

    @Serializable
    object Profile : Screen("profile")

    @Serializable
    object PropertyRegistrationPreview : Screen("propertyRegistrationPreview")
    @Serializable
    object MobileLogin : Screen("mobile_login")
    @Serializable
    object OtpLogin : Screen("otp_login/{phoneNumber}")
}