plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-interop-rx3"
}

appyx {
    namespace.set("com.bumble.appyx.utils.interop.rx3")
}

dependencies {
    api(project(":appyx-navigation:appyx-navigation"))
    api(libs.rxjava3)
    api(libs.rxrelay3)

    implementation(libs.androidx.lifecycle.java8)
}
