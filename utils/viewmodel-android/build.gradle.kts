plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-node-viewmodel"
}

appyx {
    namespace.set("com.bumble.appyx.utils.viewmodel")

    buildFeatures {
        compose.set(true)
    }
}

dependencies {
    api(project(":appyx-navigation:appyx-navigation"))
    api(project(":appyx-interactions:appyx-interactions"))
    api(libs.androidx.activity)
    api(libs.androidx.appcompat)
    api(libs.androidx.lifecycle.viewmodel)
    debugApi(libs.compose.runtime)
    implementation(libs.androidx.core.core)
    implementation(libs.androidx.lifecycle.common)
    releaseImplementation(libs.compose.runtime)
}
