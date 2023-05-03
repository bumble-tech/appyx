plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
    id("appyx-publish-android")
    id("appyx-lint")
    id("appyx-detekt")
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        namespace = "com.bumble.appyx.dagger.hilt"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    api(project(":libraries:dagger-hilt:common"))
    implementation(libs.dagger.hilt.runtime)
    api(project(":libraries:core"))
    api(libs.compose.runtime)
    implementation(libs.compose.ui.ui)
    api(libs.kotlin.reflect)
    kapt(libs.dagger.hilt.compiler)
}
