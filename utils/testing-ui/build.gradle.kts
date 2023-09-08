plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-testing-ui"
}

appyx {
    namespace.set("com.bumble.appyx.utils.testing.ui")

    buildFeatures {
        compose.set(true)
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    api(composeBom)

    api(project(":appyx-navigation:appyx-navigation"))
    api(project(":utils:testing-ui-activity"))
    api(libs.androidx.test.rules)
    api(libs.compose.ui.test.junit4)

    implementation(libs.androidx.lifecycle.java8)
}
