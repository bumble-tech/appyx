import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("com.bumble.appyx.multiplatform")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jvmTarget.get()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":appyx-interactions:appyx-interactions"))
                implementation(project(":appyx-components:standard:spotlight:spotlight"))
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.kotlin.coroutines.swing)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":demos:common"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.bumble.appyx.interactions.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AppyxDesktop"
            packageVersion = properties["library.version"].toString().split("-")[0]
        }
        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
        }
    }
}

dependencies {
    add("kspDesktop", project(":ksp:appyx-processor"))
}
