plugins {
    id("com.android.library")
    id("kotlin-android")
    id("appyx-publish-android")
    id("appyx-lint")
    id("appyx-detekt")
}

publishingPlugin {
    artifactId = "utils-testing-ui-activity"
}

android {
    namespace = "com.bumble.appyx.utils.testing.ui.activity"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    api(project(":appyx-navigation:common"))
    implementation(libs.androidx.activity.compose)
    api(project(":appyx-navigation:android"))
}
