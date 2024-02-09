plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-ribs-helpers"
}

appyx {
    namespace.set("com.bumble.appyx.utils.ribs.helpers")
}

dependencies {
    api(project(":appyx-navigation:appyx-navigation"))
    api(project(":appyx-interactions:appyx-interactions"))
}
