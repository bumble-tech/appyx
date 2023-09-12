plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-testing-ui-activity"
}

appyx {
    namespace.set("com.bumble.appyx.utils.testing.ui.activity")

    buildFeatures {
        compose.set(true)
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    api(project(":appyx-navigation:appyx-navigation"))
}
