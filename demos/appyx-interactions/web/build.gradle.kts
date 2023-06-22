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
                implementation(project(":appyx-components:internal:test-drive:test-drive"))
                implementation(project(":appyx-components:experimental:cards:cards"))
                implementation(project(":appyx-components:experimental:promoter:promoter"))
            }
        }
    }
}

compose.experimental {
    web.application {}
}
