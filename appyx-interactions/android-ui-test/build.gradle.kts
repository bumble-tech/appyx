plugins {
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("android")
    id("appyx-screenshots")
}

android {
    namespace = "com.bumble.appyx.interactions.ui.checks"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        testApplicationId = "com.bumble.appyx.interactions.ui.checks.test"

        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    testOptions {
        managedDevices {
            devices {
                maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("pixel2api30").apply {
                    // Use device profiles you typically see in Android Studio.
                    device = "Pixel 2"
                    // Use only API levels 27 and higher.
                    apiLevel = 30
                    // To include Google services, use "google".
                    systemImageSource = "aosp"
                }
            }
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
}



dependencies {
    val composeBom = platform(libs.compose.bom)

    implementation(project(":appyx-interactions:android"))
    implementation(project(":appyx-interactions:common"))
    implementation(project(":samples:common"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.tooling)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestUtil(libs.androidx.test.utils)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
}
