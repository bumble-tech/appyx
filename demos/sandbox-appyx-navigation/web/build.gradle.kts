import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    id("com.bumble.appyx.multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        moduleName = "appyx-demos-sandbox-navigation-web"
        browser()
        binaries.executable()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "appyx-demos-sandbox-navigation-web"
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":demos:common"))
                implementation(project(":demos:sandbox-appyx-navigation:common"))
                implementation(project(":appyx-interactions:appyx-interactions"))
                implementation(project(":appyx-navigation:appyx-navigation"))
                implementation(project(":appyx-components:stable:backstack:backstack"))
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation(libs.kotlin.coroutines.core)
            }
        }
    }
}

compose.experimental {
    web.application {}
}
