plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.bumble.appyx.sandbox2"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.bumble.appyx.sandbox2"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    implementation(composeBom)
    implementation(project(":libraries:core"))
    implementation(project(":samples:navmodel-samples"))
    implementation(project(":samples:common"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.ui)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.compose.ui.test.junit4)
}
