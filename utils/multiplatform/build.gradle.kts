plugins {
    id("com.bumble.appyx.multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("kotlin-parcelize")
    id("appyx-publish-multiplatform")
}

appyx {
    androidNamespace.set("com.bumble.appyx.utils.multiplatform")
}

publishingPlugin {
    artifactId = "utils-multiplatform"
}

kotlin {
    android {
        publishLibraryVariants("release")
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jvmTarget.get()
        }
    }
    js(IR) {
        // Adding moduleName as a workaround for this issue: https://youtrack.jetbrains.com/issue/KT-51942
        moduleName = "appyx-utils-multiplatform"
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.androidx.appcompat)
            }
        }
        val desktopMain by getting
        val jsMain by getting
    }
}
