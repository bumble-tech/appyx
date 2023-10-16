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
        summary = "appyx-navigation iOS module"
        homepage = "https://bumble-tech.github.io/appyx/navigation/"
        ios.deploymentTarget = "17.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ios"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['build/processedResources/ios/main/**']"
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
                implementation(project(":demos:appyx-navigation:common"))
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation(libs.kotlin.coroutines.core)
            }
        }
    }
}

compose.experimental {
    uikit.application {
        projectName = "Appyx"
        bundleIdPrefix = "com.bumble.appyx"
    }
}

tasks.register<Copy>("copyResources") {
    // Dirs containing files we want to copy
    from("../common/src/commonMain/resources")

    // Output for iOS resources
    into("$buildDir/processedResources/ios/main")

    include("**/*")
}

tasks.named("compileKotlinIosArm64") {
    dependsOn("copyResources")
}

tasks.named("compileKotlinIosSimulatorArm64") {
    dependsOn("copyResources")
}

tasks.named("compileKotlinIosX64") {
    dependsOn("copyResources")
}

dependencies {
    add("kspIosArm64", project(":ksp:mutable-ui-processor"))
    add("kspIosX64", project(":ksp:mutable-ui-processor"))
    add("kspIosSimulatorArm64", project(":ksp:mutable-ui-processor"))
}
