plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("appyx-publish-android")
    id("appyx-lint")
    id("appyx-detekt")
}

android {
    namespace = "com.bumble.appyx.interop.ribs"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    api(project(":libraries:core"))
    api(libs.ribs.base)

    val composeBom = platform(libs.compose.bom)

    implementation(composeBom)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.ui)
    implementation(libs.ribs.compose)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.activity.compose)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.compose.foundation.layout)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.ribs.base.test.activity)
}
