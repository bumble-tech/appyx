plugins {
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("android")
    id("appyx-screenshots")
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
    val composeBom = platform(libs.compose.bom)

    androidTestImplementation(project(":appyx-interactions:android"))
    androidTestImplementation(project(":appyx-interactions:common"))
    androidTestImplementation(project(":samples:common"))
    androidTestImplementation(libs.androidx.activity.compose)
    androidTestImplementation(libs.compose.ui.tooling)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestUtil(libs.androidx.test.utils)
    debugImplementation(libs.compose.ui.test.manifest)
}
