plugins {
    id("com.bumble.appyx.android.library")
    id("org.jetbrains.compose")
    id("appyx-screenshots")
}

appyx {
    namespace.set("com.bumble.appyx.components.spotlight.android")
}

dependencies {
    val composeBom = platform(libs.compose.bom)


    androidTestImplementation(composeBom)

    androidTestImplementation(project(":appyx-components:standard:spotlight:spotlight"))
    androidTestImplementation(project(":appyx-interactions:android"))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.junit.api)
    androidTestImplementation(libs.compose.ui.test.manifest)
}
