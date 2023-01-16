plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.bumble.appyx.interactions"
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
    api(libs.kotlin.coroutines.android)
    api(libs.compose.animation.core)
    api(libs.compose.runtime)
    api(libs.compose.foundation)
    api(libs.compose.ui.tooling)
    api(libs.compose.ui.ui)

    implementation(composeBom)
    implementation(libs.compose.foundation.layout)

    testImplementation(project(":libraries:testing-junit4"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
}
