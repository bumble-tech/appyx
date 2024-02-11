plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-interop-ribs"
}

appyx {
    namespace.set("com.bumble.appyx.utils.interop.ribs")

    buildFeatures {
        compose.set(true)
        kotlinParcelize.set(true)
    }
}

dependencies {
    api(project(":appyx-navigation:appyx-navigation"))
    api(libs.ribs.base)
    api(libs.ribs.compose)

    val composeBom = platform(libs.compose.bom)

    implementation(composeBom)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.activity.compose)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.compose.foundation.layout)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.ribs.base.test.activity)
}
