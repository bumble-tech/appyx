plugins {
    id("com.bumble.appyx.multiplatform")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0.0"
        summary = "appyx-interactions ios module"
        homepage = "https://bumble-tech.github.io/appyx/interactions/"
        ios.deploymentTarget = "16.4"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ios"
            isStatic = true
        }
        license = "Apache License, Version 2.0"
        authors = "https://github.com/bumble-tech/"
    }

    sourceSets {
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(project(":appyx-components:stable:spotlight:spotlight"))
                implementation(project(":appyx-components:stable:backstack:backstack"))
            }
        }
    }
}

compose.experimental {
//    uikit.application {
//        projectName = "Appyx"
//        bundleIdPrefix = "com.bumble.appyx"
//    }
}

dependencies {
    add("kspIosArm64", project(":ksp:mutable-ui-processor"))
    add("kspIosX64", project(":ksp:mutable-ui-processor"))
    add("kspIosSimulatorArm64", project(":ksp:mutable-ui-processor"))
}
