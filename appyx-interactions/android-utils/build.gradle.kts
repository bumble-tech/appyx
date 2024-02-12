plugins {
    id("com.bumble.appyx.android.library")
}

appyx {
    namespace.set("com.bumble.appyx.interactions.utils")

    buildFeatures {
        compose.set(true)
        kotlinParcelize.set(true)
    }
}

dependencies {

    api(project(":appyx-interactions:appyx-interactions"))
    api(libs.compose.material3)

    val composeBom = platform(libs.compose.bom)

    implementation(composeBom)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui.test.junit4)
    implementation(libs.androidx.test.espresso.core)
}
