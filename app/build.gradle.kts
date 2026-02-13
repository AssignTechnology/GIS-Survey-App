plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.3.2"
    id ("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.58"}

android {
    namespace = "com.example.npmlbd"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.npmlbd"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.exifinterface)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)




    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Hilt Navigation for Compose
    implementation(libs.androidx.hilt.navigation.compose)

    //serialization
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.material.icons.extended)

    // use for image
    implementation(libs.coil.compose)


    dependencies {
        // The following line is optional, as the core library is included indirectly by camera-camera2
        implementation(libs.androidx.camera.core)
        implementation(libs.androidx.camera.camera2)
        // If you want to additionally use the CameraX Lifecycle library
        implementation(libs.androidx.camera.lifecycle)
        // If you want to additionally use the CameraX VideoCapture library
        implementation(libs.androidx.camera.video)
        // If you want to additionally use the CameraX View class
        implementation(libs.androidx.camera.view)
        // If you want to additionally add CameraX ML Kit Vision Integration
        implementation(libs.androidx.camera.mlkit.vision)
        // If you want to additionally use the CameraX Extensions library
        implementation(libs.androidx.camera.extensions)
    }
     //theme management
    implementation(libs.androidx.datastore.preferences)

     //location dependency
    implementation(libs.play.services.location)

    implementation("com.google.accompanist:accompanist-permissions:0.37.3")


    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation("com.google.android.gms:play-services-auth-api-phone:18.0.1")

    implementation("com.google.accompanist:accompanist-webview:0.36.0")

    implementation("androidx.compose.foundation:foundation:1.9.0")
// For HorizontalPager

    implementation("com.google.accompanist:accompanist-pager-indicators:0.36.0")



    implementation("androidx.core:core-ktx:1.17.0")
// For Kotlin extensions
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
// For coroutines

    // Biometric library (use latest stable)
    implementation("androidx.biometric:biometric:1.1.0")

    implementation("com.airbnb.android:lottie-compose:6.6.7")
}
