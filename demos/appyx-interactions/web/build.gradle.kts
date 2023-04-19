plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
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

compose.experimental {
    web.application {}
}