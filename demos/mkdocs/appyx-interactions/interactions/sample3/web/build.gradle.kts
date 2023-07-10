plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

kotlin {
    js(IR) {
        // Adding moduleName as a workaround for this issue: https://youtrack.jetbrains.com/issue/KT-51942
        moduleName = "appyx-interactions-interactions-sample3-web"
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
            }
        }
    }
}

compose.experimental {
    web.application {}
}

dependencies {
    add("kspCommonMainMetadata", project(":ksp:mutable-ui-processor"))
    add("kspJs", project(":ksp:mutable-ui-processor"))
}
