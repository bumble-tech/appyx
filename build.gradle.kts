import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath(libs.plugin.android)
        classpath(libs.plugin.kotlin)
    }
}

plugins {
    id("appyx-collect-sarif")
    id("com.autonomousapps.dependency-analysis") version libs.versions.dependencyAnalysis.get()
    id("release-dependencies-diff-compare")
    id("release-dependencies-diff-create") apply false
    alias(libs.plugins.compose.compiler) apply false
}

dependencyAnalysis {
    issues {
        all {
            onIncorrectConfiguration {
                severity("fail")
            }
            onUnusedDependencies {
                severity("fail")

                exclude(
                    // Needed for compose '@Preview'. The annotation is actually within
                    // androidx.compose.ui:ui-tooling-preview, hence the need to exclude.
                    "androidx.compose.ui:ui-tooling",
                    "androidx.test.ext:junit",

                    // This is used to add the testing activity to the debug manifest
                    // However since not code is referenced, it is raised as unused.
                    ":libraries:testing-ui-activity",
                    ":libraries:testing-ui"
                )
            }
            onRuntimeOnly {
                exclude("org.jetbrains.kotlinx:kotlinx-coroutines-android")
            }
        }
        project(":libraries:interop-ribs") {
            onAny {
                severity("ignore")
            }
        }
        project(":samples:app") {
            onAny {
                severity("ignore")
            }
        }
        project(":samples:navigation-compose") {
            onAny {
                severity("ignore")
            }
        }
        project(":samples:sandbox") {
            onAny {
                severity("ignore")
            }
        }
        project(":libraries:testing-junit4") {
            onUnusedDependencies {
                severity("fail")
                // Not used by the module, but exposed via api to avoid adding two dependencies.
                exclude(":libraries:testing-unit-common")
            }
        }
        project(":libraries:testing-junit5") {
            onUnusedDependencies {
                severity("fail")
                // Not used by the module, but exposed via api to avoid adding two dependencies.
                exclude(":libraries:testing-unit-common")
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// TODO: Enable K2 in a later PR
tasks.withType(KotlinJvmCompile::class.java).configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
        apiVersion.set(KotlinVersion.KOTLIN_1_9)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)
    }
}

allprojects {
    configurations.all {
        resolutionStrategy {
            failOnNonReproducibleResolution()
            dependencySubstitution {
                substitute(module("com.bumble.appyx:customisations"))
                    .using(project(":libraries:customisations"))
                    .because("RIBs uses Appyx customisations as external dependency")
            }
            eachDependency {
                when (requested.group) {
                    // Version 1.0 and 1.1 are included which cause ':libraries:core' espresso tests to fail
                    "androidx.tracing" -> useVersion(libs.versions.androidx.tracing.get())
                }
            }
        }
    }
}

subprojects {
    plugins.apply("release-dependencies-diff-create")

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            if (project.findProperty("enableComposeCompilerReports") == "true") {
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                            File(project.buildDir, "compose_metrics").absolutePath

                )
                freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                            File(project.buildDir, "compose_metrics").absolutePath
                )
            }
        }
    }
}
