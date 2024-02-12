plugins {
    id("com.bumble.appyx.android.library")
    id("org.jetbrains.compose")
}

appyx {
    namespace.set("com.bumble.appyx.interactions.android")
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    implementation(composeBom)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.lifecycle.java8)

    implementation(project(":appyx-interactions:appyx-interactions"))
    androidTestImplementation(libs.compose.ui.test.junit4.android)
    androidTestImplementation(libs.compose.ui.test.junit4)

    debugRuntimeOnly(libs.compose.ui.test.manifest)
}
