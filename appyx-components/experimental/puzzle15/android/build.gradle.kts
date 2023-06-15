plugins {
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("android")
}


android {
    namespace = "com.bumble.appyx.components.experimental.puzzle15"
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
    implementation(project(":appyx-components:experimental:puzzle15:puzzle15"))
    implementation(project(":appyx-interactions:android"))
}
