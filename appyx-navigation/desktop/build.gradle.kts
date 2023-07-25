plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}


kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jvmTarget.get()
        }
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":appyx-navigation:appyx-navigation"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}
