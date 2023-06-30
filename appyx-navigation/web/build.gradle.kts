plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}


kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xlazy-ir-for-caches=disable"
        }
    }
    js(IR) {
        // Adding moduleName as a workaround for this issue: https://youtrack.jetbrains.com/issue/KT-51942
        moduleName = "appyx-navigation-js"
        browser()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":appyx-navigation:common"))
            }
        }
        val jsTest by getting
    }
}
