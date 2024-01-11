plugins {
    id("com.bumble.appyx.multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("kotlin-parcelize")
    id("appyx-publish-multiplatform")
    id("com.google.devtools.ksp")
}

appyx {
    androidNamespace.set("com.bumble.appyx.components.experimental.modal")
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
        moduleName = "appyx-components-modal-commons"
        browser()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":appyx-interactions:appyx-interactions"))
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
            }
        }
        val desktopMain by getting
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

dependencies {
    add("kspCommonMainMetadata", project(":ksp:appyx-compiler"))
    add("kspAndroid", project(":ksp:appyx-compiler"))
    add("kspDesktop", project(":ksp:appyx-compiler"))
    add("kspJs", project(":ksp:appyx-compiler"))
    add("kspIosArm64", project(":ksp:appyx-compiler"))
    add("kspIosX64", project(":ksp:appyx-compiler"))
    add("kspIosSimulatorArm64", project(":ksp:appyx-compiler"))
}
