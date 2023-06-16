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
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation(project(":appyx-interactions:appyx-interactions"))
                implementation(project(":appyx-components:experimental:puzzle15:puzzle15"))
                implementation(project(":appyx-components:internal:test-drive:test-drive"))
            }
        }
    }
}

compose.experimental {
    web.application {}
}
