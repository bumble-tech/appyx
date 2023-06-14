plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("appyx-lint")
    id("appyx-detekt")
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    namespace = "com.bumble.appyx.demos.appyxnavigation"

    defaultConfig {
        applicationId = "com.bumble.demos.samples.appyxnavigation"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        create("sampleConfig") { // debug is already created
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.findByName("sampleConfig")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // if we ever publish, we should create a more secure signingConfig
            signingConfig = signingConfigs.findByName("sampleConfig")
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    implementation(composeBom)
    implementation(project(":appyx-navigation:android"))
    implementation(project(":demos:common"))
    implementation(project(":appyx-interactions:appyx-interactions"))
    implementation(project(":appyx-components:spotlight:spotlight"))
    implementation(project(":appyx-components:backstack:backstack"))
    implementation(project(":appyx-components:demos:demos"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.ui)
    implementation(libs.google.material)
    implementation(libs.coil.compose)
}
