plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

kotlin {
    android()
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(mapOf("path" to ":appyx-interactions:common")))
                implementation(project(":appyx-interactions:common"))
            }
        }
        val jsMain by getting  {
            dependencies {
                implementation(project(mapOf("path" to ":appyx-interactions:common")))
                implementation(project(":appyx-interactions:common"))
            }
        }
    }
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
    }
}

compose.experimental {
    web.application {}
}

