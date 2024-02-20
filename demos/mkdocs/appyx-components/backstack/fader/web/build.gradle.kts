plugins {
    id("com.bumble.appyx.multiplatform")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

kotlin {
    js(IR) {
        moduleName = "appyx-demos-backstack-fader-web"
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
                implementation(project(":appyx-components:standard:backstack:backstack"))
                implementation(project(":demos:mkdocs:appyx-components:common"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(project(":demos:mkdocs:common"))
            }
        }
    }
}

compose.experimental {
    web.application {}
}

dependencies {
    add("kspCommonMainMetadata", project(":ksp:appyx-processor"))
    add("kspJs", project(":ksp:appyx-processor"))
}
