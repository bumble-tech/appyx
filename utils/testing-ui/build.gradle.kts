plugins {
    id("com.android.library")
    id("kotlin-android")
    id("appyx-publish-android")
    id("appyx-lint")
    id("appyx-detekt")
}

publishingPlugin {
    artifactId = "utils-testing-ui"
}

android {
    namespace = "com.bumble.appyx.utils.testing.ui"
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
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    api(composeBom)

    api(project(":appyx-navigation:appyx-navigation"))
    api(project(":utils:testing-ui-activity"))
    api(libs.androidx.test.rules)
    api(libs.compose.ui.test.junit4)

    implementation(libs.androidx.lifecycle.java8)
}
