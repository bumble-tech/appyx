plugins {
    id("com.android.library")
    id("kotlin-android")
    id("appyx-publish-android")
    id("appyx-lint")
    id("appyx-detekt")
}

android {
    namespace = "com.bumble.appyx.interop.rx2"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    api(project(":libraries:core"))
    api(libs.rxjava2)
    api(libs.rxrelay)

    implementation(libs.kotlin.coroutines.rx2)
    implementation(libs.androidx.lifecycle.java8)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}
