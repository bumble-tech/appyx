plugins {
    id("com.bumble.appyx.android.library")
    id("org.jetbrains.compose")
}

appyx {
    namespace.set("com.bumble.appyx.components.experimental.promoter.android")
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    api(project(":appyx-interactions:android"))

    implementation(project(":appyx-interactions:appyx-interactions"))
    implementation(project(":appyx-components:experimental:promoter:promoter"))
    implementation(composeBom)

    androidTestImplementation(libs.junit.api)
    androidTestImplementation(libs.compose.ui.test.manifest)
}
