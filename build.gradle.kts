import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform") apply false
    kotlin("plugin.serialization") version "1.8.10" apply false
    id("appyx-collect-sarif")
    id("com.android.application") version "7.3.0" apply false
    id("com.autonomousapps.dependency-analysis") version libs.versions.dependencyAnalysis.get()
    id("org.jetbrains.compose") apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("release-dependencies-diff-compare")
    id("release-dependencies-diff-create") apply false
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

                    // This is used to add the testing activity to the debug manifest
                    // However since not code is referenced, it is raised as unused.
                    ":utils:testing-ui-activity"
                )
            }
        }
        project(":utils:testing-junit4") {
            onUnusedDependencies {
                severity("fail")
                // Not used by the module, but exposed via api to avoid adding two dependencies.
                exclude(":utils:testing-unit-common")
            }
        }
        project(":utils:testing-junit5") {
            onUnusedDependencies {
                severity("fail")
                // Not used by the module, but exposed via api to avoid adding two dependencies.
                exclude(":utils:testing-unit-common")
            }
        }
    }
}

allprojects {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("com.bumble.appyx:customisations"))
                .using(project(":utils:customisations"))
                .because("RIBs uses Appyx customisations as external dependency")
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
