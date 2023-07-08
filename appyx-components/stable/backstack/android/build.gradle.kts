plugins {
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("android")
}


android {
    namespace = "com.bumble.appyx.components.backstack.android"
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
    packaging {
        resources.excludes.apply {
            add("META-INF/LICENSE.md")
            add("META-INF/LICENSE-notice.md")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    androidTestImplementation(composeBom)
    androidTestImplementation(project(":appyx-components:stable:backstack:backstack"))
    androidTestImplementation(project(":appyx-interactions:android"))
    androidTestImplementation(project(":appyx-interactions:appyx-interactions"))
    androidTestImplementation(libs.compose.ui.test.junit4)

    androidTestImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.vintage)

    androidTestImplementation(libs.compose.ui.test.manifest)
}
