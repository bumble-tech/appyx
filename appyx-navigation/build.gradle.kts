plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("appyx-publish-android")
    id("appyx-lint")
    id("appyx-detekt")
}

android {
    namespace = "com.bumble.appyx.navigation"
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
    api(project(":libraries:customisations"))
    api(project(":appyx-interactions:common"))
    api(libs.kotlin.coroutines.android)
    api(libs.androidx.lifecycle.common)
    api(libs.compose.runtime)
    api(libs.compose.ui.tooling)
    api(libs.compose.ui.ui)
    api(libs.androidx.appcompat)

    implementation(composeBom)
    implementation(libs.compose.animation.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.compose.foundation.layout)

    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.junit)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.compose.foundation)
    androidTestImplementation(project(":libraries:testing-ui"))
}
