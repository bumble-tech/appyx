plugins {
    id("com.bumble.appyx.android.application")
}

appyx {
    namespace.set("com.bumble.appyx.demos.appyxinteractions")

    buildFeatures {
        compose.set(true)
        kotlinParcelize.set(true)
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)

    implementation(composeBom)
    implementation(project(":appyx-interactions:android"))
    implementation(project(":appyx-components:stable:backstack:backstack"))
    implementation(project(":appyx-components:stable:spotlight:spotlight"))
    implementation(project(":appyx-components:experimental:cards:android"))
    implementation(project(":appyx-components:experimental:modal:modal"))
    implementation(project(":appyx-components:experimental:puzzle15:android"))
    implementation(project(":appyx-components:internal:test-drive:android"))
    implementation(project(":utils:material3"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.ui)

    implementation(libs.compose.material.icons.extended)
    implementation(libs.google.material)
    implementation(libs.compose.material3)
}
