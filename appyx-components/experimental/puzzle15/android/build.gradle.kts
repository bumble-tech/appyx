plugins {
    id("com.bumble.appyx.android.library")
    id("org.jetbrains.compose")
}

appyx {
    namespace.set("com.bumble.appyx.components.experimental.puzzle15.android")
}

dependencies {
    implementation(project(":appyx-components:experimental:puzzle15:puzzle15"))
    implementation(project(":appyx-interactions:android-utils"))
}
