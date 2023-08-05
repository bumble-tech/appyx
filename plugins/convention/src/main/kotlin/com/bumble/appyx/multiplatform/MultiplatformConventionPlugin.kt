package com.bumble.appyx.multiplatform

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("appyx", MultiplatformConventionExtension::class.java)

        project.plugins.apply("kotlin-multiplatform")
        project.plugins.apply("org.jetbrains.compose")
        project.plugins.apply("appyx-detekt")

        project.extensions.configure<DetektExtension> {
            source.setFrom(
                "src/androidMain/kotlin",
                "src/androidTest/kotlin",
                "src/commonMain/kotlin",
                "src/commonTest/kotlin",
                "src/desktopMain/kotlin",
                "src/desktopTest/kotlin",
                "src/jsMain/kotlin",
                "src/jsTest/kotlin",
            )
        }
    }
}
