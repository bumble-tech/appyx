plugins {
    id("org.jetbrains.compose")
    id("com.android.library")
    id("appyx-screenshots")
    kotlin("android")
}

android {
    namespace = "com.bumble.appyx.interactions.ui.checks"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        testApplicationId = "com.bumble.appyx.interactions.ui.checks.test"

        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}



dependencies {
    androidTestImplementation(project(":appyx-interactions:android"))
    androidTestImplementation(project(":appyx-interactions:common"))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
