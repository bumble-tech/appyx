plugins {
    id("com.android.library")
    id("kotlin-android")
    id("appyx-publish-android")
    id("appyx-lint")
    id("appyx-detekt")
}

publishingPlugin {
    artifactId = "utils-interop-rx2"
}

android {
    namespace = "com.bumble.appyx.utils.interop.rx2"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(project(":appyx-navigation"))
    api(libs.rxjava2)
    api(libs.rxrelay2)

    implementation(libs.kotlin.coroutines.rx2)
    implementation(libs.androidx.lifecycle.java8)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}
