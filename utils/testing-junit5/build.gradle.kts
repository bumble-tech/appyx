plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-testing-junit5"
}

appyx {
    namespace.set("com.bumble.appyx.utils.testing.junit5")

    buildFeatures {
        compose.set(true)
    }
}

dependencies {
    api(project(":utils:testing-unit-common"))
    api(libs.junit.api)
    api(libs.kotlin.coroutines.test.jvm)
}
