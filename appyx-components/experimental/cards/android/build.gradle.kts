plugins {
    id("com.bumble.appyx.android.library")
    id("org.jetbrains.compose")
}

appyx {
    namespace.set("com.bumble.appyx.components.experimental.cards.android")
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    implementation(project(":demos:common"))

    implementation(project(":appyx-interactions:android"))
    implementation(project(":appyx-interactions:appyx-interactions"))
    implementation(project(":appyx-components:experimental:cards:cards"))
    implementation(composeBom)

    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.junit.api)
    androidTestImplementation(libs.compose.ui.test.manifest)
}
