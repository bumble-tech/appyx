plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(libs.plugins.detekt)
}

dependencies {
    implementation(libs.plugin.android)
    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.detekt)
    implementation(project(":publish-plugin"))
}

detekt {
    buildUponDefaultConfig = true
    config.from(file("../../detekt.yml"))
}

gradlePlugin {
    plugins {
        create("com.bumble.appyx.android.application") {
            id = "com.bumble.appyx.android.application"
            implementationClass = "com.bumble.appyx.android.application.AndroidApplicationConventionPlugin"
        }
        create("com.bumble.appyx.android.library") {
            id = "com.bumble.appyx.android.library"
            implementationClass = "com.bumble.appyx.android.library.AndroidLibraryConventionPlugin"
        }
        create("com.bumble.appyx.multiplatform") {
            id = "com.bumble.appyx.multiplatform"
            implementationClass = "com.bumble.appyx.multiplatform.MultiplatformConventionPlugin"
        }
    }
}
