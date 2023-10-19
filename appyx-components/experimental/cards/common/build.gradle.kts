plugins {
    id("com.bumble.appyx.multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("kotlin-parcelize")
    id("appyx-publish-multiplatform")
    id("com.google.devtools.ksp")
}

appyx {
    androidNamespace.set("com.bumble.appyx.components.experimental.cards.common")
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
        moduleName = "appyx-components-experimental-cards-commons"
        browser()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":appyx-interactions:appyx-interactions"))
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
        val androidMain by getting
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
    add("kspCommonMainMetadata", project(":ksp:mutable-ui-processor"))
    add("kspAndroid", project(":ksp:mutable-ui-processor"))
    add("kspDesktop", project(":ksp:mutable-ui-processor"))
    add("kspJs", project(":ksp:mutable-ui-processor"))
    add("kspIosArm64", project(":ksp:mutable-ui-processor"))
    add("kspIosX64", project(":ksp:mutable-ui-processor"))
    add("kspIosSimulatorArm64", project(":ksp:mutable-ui-processor"))
}
