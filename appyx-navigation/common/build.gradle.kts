plugins {
    id("com.bumble.appyx.multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("appyx-publish-multiplatform")
}

appyx {
    androidNamespace.set("com.bumble.appyx.navigation")
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
        moduleName = "appyx-navigation-common"
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(project(":utils:multiplatform"))
                implementation(libs.kotlinx.serialization.json)
                api(project(":utils:customisations"))
                api(project(":appyx-interactions:appyx-interactions"))
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
                api(libs.compose.runtime)
                api(libs.compose.ui.tooling)

                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.lifecycle.java8)

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

    dependencies {
        val composeBom = platform(libs.compose.bom)

        api(composeBom)

        androidTestImplementation(composeBom)
        androidTestImplementation(libs.androidx.test.espresso.core)
        androidTestImplementation(libs.androidx.test.junit)
        androidTestImplementation(libs.compose.ui.test.junit4)
        androidTestImplementation(libs.compose.foundation)
        androidTestImplementation(project(":utils:testing-ui"))
    }
}
