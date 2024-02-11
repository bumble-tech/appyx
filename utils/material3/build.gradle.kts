plugins {
    id("com.bumble.appyx.multiplatform")
    id("com.android.library")
    id("appyx-publish-multiplatform")
    id("org.jetbrains.compose")
}

appyx {
    androidNamespace.set("com.bumble.appyx.utils.material3")
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
        moduleName = "appyx-utils-material3"
        browser()
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(project(":appyx-navigation:appyx-navigation"))
                implementation(project(":appyx-components:standard:spotlight:spotlight"))
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
