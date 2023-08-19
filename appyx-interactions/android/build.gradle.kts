plugins {
    id("com.bumble.appyx.android.library")
    id("org.jetbrains.compose")
}


android {
    namespace = "com.bumble.appyx.interactions.android"
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

    api(project(":appyx-interactions:appyx-interactions"))
    api(libs.compose.ui.test.junit4)
    implementation(libs.androidx.test.core)

    implementation(composeBom)
    androidTestImplementation(composeBom)

    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.vintage)

    debugRuntimeOnly(libs.compose.ui.test.manifest)
}
