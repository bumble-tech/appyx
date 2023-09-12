plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-interop-rx2"
}

appyx {
    namespace.set("com.bumble.appyx.utils.interop.rx2")
}

dependencies {
    api(project(":appyx-navigation:appyx-navigation"))
    api(libs.rxjava2)
    api(libs.rxrelay2)

    implementation(libs.kotlin.coroutines.rx2)
    implementation(libs.androidx.lifecycle.java8)
}
