import org.jetbrains.kotlin.config.JvmTarget

plugins {
    id("com.android.library")
    alias(libs.plugins.compose.compiler)
    id("kotlin-android")
    id("appyx-publish")
    id("appyx-lint")
    id("appyx-detekt")
}

android {
    namespace = "com.bumble.appyx.testing.ui"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_11.toString()
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    api(composeBom)

    api(project(":libraries:core"))
    api(project(":libraries:testing-ui-activity"))
    api(libs.androidx.test.rules)
    api(libs.compose.ui.test.junit4)

    implementation(libs.androidx.lifecycle.java8)
}
