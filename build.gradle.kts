import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val compose_ui_version by extra("1.2.0")
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
    id("com.android.application") version "7.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
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
                    ":libraries:testing-ui-activity"
                )
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

allprojects {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("com.bumble.appyx:customisations"))
                .using(project(":libraries:customisations"))
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
