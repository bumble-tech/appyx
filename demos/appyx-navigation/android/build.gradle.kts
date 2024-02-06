plugins {
    id("com.bumble.appyx.android.application")
}

appyx {
    namespace.set("com.bumble.appyx.demos.navigation")

    buildFeatures {
        compose.set(true)
        kotlinParcelize.set(true)
    }
}

android {
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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // if we ever publish, we should create a more secure signingConfig
            signingConfig = signingConfigs.findByName("sampleConfig")
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    implementation(composeBom)
    implementation(project(":demos:appyx-navigation:common"))
    implementation(project(":appyx-components:standard:backstack:backstack"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling)
    implementation(libs.google.material)
}
