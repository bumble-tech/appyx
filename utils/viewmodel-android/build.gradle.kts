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
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.viewmodel.ktx)
}
