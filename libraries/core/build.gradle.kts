plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("appyx-publish-android")
    id("appyx-lint")
    id("appyx-detekt")
}

android {
    namespace = "com.bumble.appyx.core"
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
    api(project(":libraries:customisations"))
    api(libs.kotlin.coroutines.android)
    api(libs.androidx.lifecycle.common)
    api("androidx.compose.animation:animation-core")
    api("androidx.compose.runtime:runtime")
    api(libs.androidx.appcompat)

    val composeBom = platform(libs.compose.bom)

    implementation(composeBom)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.lifecycle.runtime)
    implementation("androidx.compose.foundation:foundation-layout")

    testImplementation(project(":libraries:testing-junit4"))
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.compose.foundation:foundation")
    androidTestImplementation(project(":libraries:testing-ui"))
}
