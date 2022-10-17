plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("appyx-lint")
    id("appyx-detekt")
}

android {
    namespace = "com.bumble.appyx.samples.common"
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

    api(libs.compose.ui.ui)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.material)
    implementation(libs.coil.compose)
}
