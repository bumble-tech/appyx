plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("appyx-lint")
    id("appyx-detekt")
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.bumble.appyx.sandbox"
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packagingOptions {
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(project(":libraries:core"))
    implementation(project(":libraries:interop-rx2"))
    implementation(project(":libraries:interop-ribs"))
    implementation(project(":samples:navmodel-samples"))
    implementation(project(":samples:common"))
    // The testing activity needs to be in the main manifest, otherwise it cannot be launched.
    debugImplementation(project(":libraries:testing-ui-activity"))
    implementation(libs.google.accompanist.systemui)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.ui)
    implementation(libs.google.accompanist.flow)
    implementation(libs.google.material)
    implementation(libs.ribs.base)
    implementation(libs.mvicore.base)
    implementation(libs.mvicore.android)
    implementation(libs.mvicore.binder)
    implementation(libs.rxjava2)
    implementation(libs.rxandroid2)
    implementation(libs.rxrelay2)

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.junit)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.vintage)
    testImplementation(project(":libraries:testing-junit4"))
    testImplementation(project(":libraries:testing-junit5"))
    testImplementation(libs.ribs.base.test)
    testImplementation(libs.ribs.base.test.rx2)

    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(project(":libraries:testing-ui"))
}
