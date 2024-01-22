plugins {
    id("com.bumble.appyx.android.library")
    id("org.jetbrains.compose")
}

appyx {
    namespace.set("com.bumble.appyx.navigation.android")

    buildFeatures {
        compose.set(true)
        kotlinParcelize.set(true)
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    api(libs.kotlin.coroutines.android)
    api(libs.compose.ui.tooling)

    implementation(composeBom)
    implementation(libs.androidx.lifecycle.java8)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.compose.foundation)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(project(":utils:testing-ui"))
}
