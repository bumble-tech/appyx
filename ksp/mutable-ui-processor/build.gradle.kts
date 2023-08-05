plugins {
    id("com.bumble.appyx.multiplatform")
    id("appyx-publish-multiplatform")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jvmTarget.get()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.foundation) {
                    exclude("org.jetbrains.skiko", "skiko")
                }
                api(project(":appyx-interactions:appyx-interactions")) {
                    exclude("org.jetbrains.skiko", "skiko")
                }
                implementation("com.squareup:kotlinpoet:1.12.0")
                implementation("com.squareup:kotlinpoet-ksp:1.12.0")
                implementation(libs.symbol.processing.api)
            }
        }
    }
}
