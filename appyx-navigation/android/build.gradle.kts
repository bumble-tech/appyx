plugins {
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("android")
    id("appyx-publish-android")
    id("appyx-lint")
    id("kotlin-parcelize")
    id("appyx-detekt")
}

android {
    namespace = "com.bumble.appyx.navigation.android"
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
    testOptions {
        unitTests.all {
            // interface method default implementation
            it.exclude("**/*\$DefaultImpls.class")
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    api(composeBom)
    api(project(":appyx-navigation:common"))
    runtimeOnly(libs.kotlin.coroutines.android)
    implementation(libs.androidx.lifecycle.common)

    api(libs.compose.runtime)
    api(libs.compose.ui.tooling)
    api(libs.androidx.appcompat)

    implementation(composeBom)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.java8)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.compose.foundation)
    androidTestImplementation(project(":utils:testing-ui"))
}
