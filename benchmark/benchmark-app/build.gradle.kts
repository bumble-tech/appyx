plugins {
    id("com.bumble.appyx.android.application")
    id("kotlin-parcelize")
    id("org.jetbrains.compose")
}

appyx {
    namespace.set("com.bumble.appyx.benchmark.app")
}

android {
    defaultConfig {
        applicationId = "com.bumble.appyx.benchmark.app"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("debug")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += listOf("release")
            isDebuggable = false
            signingConfig = signingConfigs.findByName("debug")
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    implementation(composeBom)
    implementation(project(":demos:appyx-navigation:common"))
    implementation(project(":appyx-components:stable:backstack:backstack"))
    api(project(":utils:multiplatform"))


    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.compose.material3)
}
