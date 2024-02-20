plugins {
    id("com.bumble.appyx.multiplatform")
    id("com.android.library")
    id("appyx-publish-multiplatform")
}

appyx {
    androidNamespace.set("com.bumble.appyx.utils.interop.coroutines")
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jvmTarget.get()
        }
    }
    js(IR) {
        // Adding moduleName as a workaround for this issue: https://youtrack.jetbrains.com/issue/KT-51942
        moduleName = "appyx-utils-coroutines"
        browser()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":appyx-navigation:appyx-navigation"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlin.coroutines.test)
            }
        }
        val androidMain by getting
        val desktopMain by getting
        val jsMain by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}
