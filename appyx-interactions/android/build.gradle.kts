plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

apply(plugin = "shot")


android {
    namespace = "com.bumble.appyx.interactions"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.bumble.appyx.interactions"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        testInstrumentationRunner = "com.karumi.shot.ShotTestRunner"
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
    debugImplementation(libs.compose.ui.test.manifest)
}
