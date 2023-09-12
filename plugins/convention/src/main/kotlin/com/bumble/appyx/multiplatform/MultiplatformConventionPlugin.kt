package com.bumble.appyx.multiplatform

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.bumble.appyx.versionCatalog
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

class MultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val conventionExtension =
            project.extensions.create("appyx", MultiplatformConventionExtension::class.java)

        project.plugins.apply("kotlin-multiplatform")
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

        project.afterEvaluate {
            project.plugins.withId("kotlin-multiplatform") {
                project.extensions
                    .getByType<KotlinMultiplatformExtension>()
                    .configure(project)
            }
        }

        project.plugins.withId("com.android.library") {
            project.extensions
                .getByType<LibraryAndroidComponentsExtension>()
                .finalizeDsl { extension ->
                    val libs = project.versionCatalog

                    with(extension) {
                        namespace = conventionExtension.androidNamespace.get()
                        compileSdk = libs.findVersion("androidCompileSdk").get().displayName.toInt()
                        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

                        defaultConfig {
                            minSdk = libs.findVersion("androidMinSdk").get().displayName.toInt()
                            targetSdk = libs.findVersion("androidTargetSdk").get().displayName.toInt()
                        }

                        compileOptions {
                            sourceCompatibility = JavaVersion.VERSION_17
                            targetCompatibility = JavaVersion.VERSION_17
                        }
                    }
                }
        }
    }

    private fun KotlinMultiplatformExtension.configure(project: Project) {
        targets {
            val jsTarget = findByName("js") as? KotlinJsTargetDsl
            if (jsTarget != null) {
                if (jsTarget.moduleName == null) {
                    throw GradleException("${project.path}: 'js' module name was not set")
                }
            }
        }
    }
}
