plugins {
    id("com.bumble.appyx.android.library")
    id("org.jetbrains.compose")
}

appyx {
    namespace.set("com.bumble.appyx.components.backstack.android")
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    androidTestImplementation(composeBom)
    androidTestImplementation(project(":appyx-components:stable:backstack:backstack"))
    androidTestImplementation(project(":appyx-interactions:android"))
    androidTestImplementation(project(":appyx-interactions:appyx-interactions"))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.junit.api)
    androidTestImplementation(libs.compose.ui.test.manifest)
}
