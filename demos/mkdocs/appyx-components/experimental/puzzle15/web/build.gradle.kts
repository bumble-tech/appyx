plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
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
                implementation(project(":demos:mkdocs:appyx-components:common"))
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
