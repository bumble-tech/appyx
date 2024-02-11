plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-testing-unit-common"
}

appyx {
    namespace.set("com.bumble.appyx.utils.testing.unit.common")

    buildFeatures {
        compose.set(true)
    }
}

dependencies {
    api(project(":appyx-navigation:appyx-navigation"))
    api(project(":utils:ribs-helpers"))
    implementation(project(":utils:utils-customisations"))
    implementation(libs.kotlin.test)
}
