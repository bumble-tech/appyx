plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}


android {
    namespace = "com.bumble.appyx.interactions"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.example.android"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
    }
}

dependencies {
    implementation(project(":appyx-interactions:common"))
    implementation(project(":samples:common"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.tooling)
}
