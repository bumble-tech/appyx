import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

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
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
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
                implementation(project(":demos:mkdocs:appyx-components:common"))
                implementation(project(":demos:mkdocs:common"))
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
    add("kspWasmJs", project(":ksp:mutable-ui-processor"))
}

tasks.register<Copy>("jsCopyResources") {
    // Dirs containing files we want to copy
    from("../common/src/commonMain/resources")

    // Output for web resources
    into("$buildDir/processedResources/js/main")

    include("**/*")
}

tasks.named("jsBrowserProductionExecutableDistributeResources") {
    dependsOn("jsCopyResources")
}

tasks.named("jsMainClasses") {
    dependsOn("jsCopyResources")
}

tasks.register<Copy>("wasmJsCopyResources") {
    // Dirs containing files we want to copy
    from("../common/src/commonMain/resources")

    // Output for web resources
    into("$buildDir/processedResources/wasmJs/main")

    include("**/*")
}

tasks.named("wasmJsBrowserProductionExecutableDistributeResources") {
    dependsOn("wasmJsCopyResources")
}

tasks.named("wasmJsMainClasses") {
    dependsOn("wasmJsCopyResources")
}
