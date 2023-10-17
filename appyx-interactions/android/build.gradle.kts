plugins {
    id("com.bumble.appyx.android.library")
    id("org.jetbrains.compose")
}

appyx {
    namespace.set("com.bumble.appyx.interactions.android")
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    api(project(":appyx-interactions:appyx-interactions"))
    api(libs.compose.ui.test.junit4.android)
    implementation(libs.androidx.test.core)
    implementation(composeBom)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.compose.ui.test.junit4)

    debugRuntimeOnly(libs.compose.ui.test.manifest)
}
