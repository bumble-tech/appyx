plugins {
    id("org.jetbrains.compose")
    id("com.android.library")
    id("appyx-screenshots")
    kotlin("android")
}


android {
    namespace = "com.bumble.appyx.components.testdrive"
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

    implementation(project(":appyx-components:internal:test-drive:test-drive"))
    implementation(project(":appyx-interactions:android"))
    implementation(composeBom)
    androidTestImplementation(libs.compose.ui.test.junit4)

    androidTestImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.vintage)

    androidTestImplementation(libs.compose.ui.test.manifest)
}
