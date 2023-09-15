plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-testing-android-viewmodel"
}

appyx {
    namespace.set("com.bumble.appyx.utils.viewmodel")

    buildFeatures {
        compose.set(true)
    }
}

dependencies {
    api(project(":appyx-navigation:appyx-navigation"))
    api("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
}
