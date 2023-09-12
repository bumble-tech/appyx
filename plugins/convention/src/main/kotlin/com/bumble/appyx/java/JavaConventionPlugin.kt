package com.bumble.appyx.java

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure

class JavaConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("appyx", JavaConventionExtension::class.java)

        project.plugins.apply("java-library")
        project.plugins.apply("kotlin")
        project.plugins.apply("appyx-detekt")

        project.extensions
            .configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
    }
}
