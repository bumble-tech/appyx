plugins {
    id("com.bumble.appyx.multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("kotlin-parcelize")
    id("appyx-publish-multiplatform")
    id("com.google.devtools.ksp")
}

appyx {
    androidNamespace.set("com.bumble.appyx.demos.appyxnavigation.common")
}

kotlin {
    android {
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
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                implementation(libs.kotlinx.serialization.json)
                api(project(":appyx-interactions:appyx-interactions"))
                api(project(":appyx-navigation:appyx-navigation"))
                api(project(":utils:customisations"))
                api(project(":utils:multiplatform"))
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
    }
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":ksp:mutable-ui-processor"))
    add("kspAndroid", project(":ksp:mutable-ui-processor"))
    add("kspDesktop", project(":ksp:mutable-ui-processor"))
    add("kspJs", project(":ksp:mutable-ui-processor"))
}
