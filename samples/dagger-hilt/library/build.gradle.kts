plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
    id("com.google.devtools.ksp") version libs.versions.ksp.get()
    id("appyx-lint")
    id("appyx-detekt")
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        namespace = "com.bumble.appyx.sample.hilt.library"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

// KSP source files don't appear to be included in sourceSets by default...
// https://github.com/google/ksp/issues/37
// https://kotlinlang.org/docs/ksp-quickstart.html#make-ide-aware-of-generated-code
androidComponents.onVariants { variant ->
    kotlin.sourceSets.findByName(variant.name)?.kotlin?.srcDirs(
        file("$buildDir/generated/ksp/${variant.name}/kotlin")
    )
}

kapt {
    correctErrorTypes = true
}

dependencies {
    api(project(":libraries:core"))
    api(project(":libraries:dagger-hilt:runtime"))
    implementation(libs.dagger.hilt.runtime)
    kapt(libs.dagger.hilt.compiler)
    ksp(project(":libraries:dagger-hilt:compiler"))

    implementation(libs.compose.ui.ui)
    implementation(libs.compose.material)
}
