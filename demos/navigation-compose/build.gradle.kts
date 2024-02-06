plugins {
    id("com.bumble.appyx.android.library")
}

appyx {
    namespace.set("com.bumble.appyx.sample.navigation.compose")

    buildFeatures {
        compose.set(true)
        kotlinParcelize.set(true)
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    implementation(project(":appyx-navigation:appyx-navigation"))
    implementation(project(":appyx-components:standard:backstack:backstack"))

    implementation(composeBom)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.compose.material3)

    androidTestImplementation(composeBom)
    androidTestImplementation(project(":utils:testing-ui-activity"))
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.compose.ui.test.junit4)
}
