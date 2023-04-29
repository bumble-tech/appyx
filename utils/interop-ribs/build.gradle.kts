plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("appyx-publish-android")
    id("appyx-lint")
    id("appyx-detekt")
}

publishingPlugin {
    artifactId = "utils-interop-ribs"
}

android {
    namespace = "com.bumble.appyx.utils.interop.ribs"
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
    api(project(":appyx-navigation"))
    api(libs.ribs.base)
    api(libs.ribs.compose)

    val composeBom = platform(libs.compose.bom)

    implementation(composeBom)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.ui)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.activity.compose)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.compose.foundation.layout)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.ribs.base.test.activity)
}
