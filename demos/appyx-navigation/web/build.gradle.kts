plugins {
    id("com.bumble.appyx.multiplatform")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

kotlin {
    js(IR) {
        moduleName = "appyx-demos-navigation-web"
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":demos:common"))
                implementation(project(":demos:appyx-navigation:common"))
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

dependencies {
    add("kspCommonMainMetadata", project(":ksp:mutable-ui-processor"))
    add("kspJs", project(":ksp:mutable-ui-processor"))
}

tasks.register<Copy>("copyResources") {
    // Dirs containing files we want to copy
    from("../common/src/commonMain/resources")

    // Output for web resources
    into("$buildDir/processedResources/js/main")

    include("**/*")
}

tasks.named("jsBrowserProductionExecutableDistributeResources") {
    dependsOn("copyResources")
}

tasks.named("jsMainClasses") {
    dependsOn("copyResources")
}
