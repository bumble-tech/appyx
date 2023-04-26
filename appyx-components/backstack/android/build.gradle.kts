plugins {
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("android")
}


android {
    namespace = "com.bumble.appyx.backstack"
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
    packagingOptions {
        resources.excludes.apply {
            add("META-INF/LICENSE.md")
            add("META-INF/LICENSE-notice.md")
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    androidTestImplementation(composeBom)
    androidTestImplementation(project(":appyx-components:backstack:backstack"))
    androidTestImplementation(project(":appyx-interactions:android"))
    androidTestImplementation(project(":appyx-interactions:appyx-interactions"))
    androidTestImplementation(libs.compose.ui.test.junit4)

    androidTestImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.vintage)

    androidTestImplementation(libs.compose.ui.test.manifest)
}
