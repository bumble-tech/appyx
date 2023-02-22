plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    id("appyx-screenshots")
    kotlin("android")
}


android {
    namespace = "com.bumble.appyx.interactions"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.bumble.appyx.interactions"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    implementation(project(":appyx-interactions:common"))
    implementation(project(":samples:common"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.tooling)


    androidTestImplementation(composeBom)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.espresso.core)

    debugImplementation(libs.compose.ui.test.manifest)
}
