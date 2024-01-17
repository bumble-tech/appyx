plugins {
    id("com.bumble.appyx.multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

appyx {
    androidNamespace.set("com.bumble.appyx.demos.navigation.common")
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jvmTarget.get()
        }
    }
    js(IR) {
        // Adding moduleName as a workaround for this issue: https://youtrack.jetbrains.com/issue/KT-51942
        moduleName = "demo-appyx-navigation-common"
        browser()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                implementation(libs.kotlinx.serialization.json)
                api(project(":appyx-interactions:appyx-interactions"))
                api(project(":utils:utils-customisations"))
                api(project(":utils:utils-material3"))
                api(project(":utils:utils-multiplatform"))
                api(project(":demos:image-loader:common"))
                implementation(project(":appyx-components:experimental:cards:cards"))
                implementation(project(":appyx-components:experimental:modal:modal"))
                implementation(project(":appyx-components:experimental:promoter:promoter"))
                implementation(project(":appyx-components:stable:backstack:backstack"))
                implementation(project(":appyx-components:stable:spotlight:spotlight"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.androidx.appcompat)
                api(libs.androidx.core)
                implementation(libs.androidx.activity.compose)
                implementation(libs.coil.compose)
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("uuid", libs.versions.uuid.get()))
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":ksp:appyx-processor"))
    add("kspAndroid", project(":ksp:appyx-processor"))
    add("kspDesktop", project(":ksp:appyx-processor"))
    add("kspJs", project(":ksp:appyx-processor"))
    add("kspIosArm64", project(":ksp:appyx-processor"))
    add("kspIosX64", project(":ksp:appyx-processor"))
    add("kspIosSimulatorArm64", project(":ksp:appyx-processor"))
}
