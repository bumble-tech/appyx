plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-testing-junit4"
}

appyx {
    namespace.set("com.bumble.appyx.utils.testing.junit4")

    buildFeatures {
        compose.set(true)
    }
}

dependencies {
    api(project(":utils:testing-unit-common"))
    api(libs.junit)
    implementation(libs.kotlin.coroutines.test)
}
