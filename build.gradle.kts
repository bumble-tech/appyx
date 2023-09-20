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
    kotlin("multiplatform") version libs.versions.kotlin.get() apply false
    kotlin("plugin.serialization") version libs.versions.kotlin.get() apply false
    id("appyx-collect-sarif")
    id("com.android.application") version libs.versions.agp.get() apply false
    id("com.google.devtools.ksp") version libs.versions.ksp.get() apply false
    id("com.autonomousapps.dependency-analysis") version libs.versions.dependencyAnalysis.get()
    id("org.jetbrains.compose") version libs.versions.composePlugin.get() apply false
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin.get() apply false
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
                    ":utils:testing-ui-activity",

                    // Convenience for convention plugins to avoid needing to define this.
                    "org.junit.jupiter:junit-jupiter-api"
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

val buildNonMkdocsTask = tasks.register("buildNonMkdocs")
val jsBrowserDistributionMkdocsTask = tasks.register("jsBrowserDistributionMkdocs")

subprojects {
    // Allows avoiding building these modules as part of CI as they are also built for mkdocs.
    if (!path.startsWith(":demos:mkdocs:")) {
        plugins.withId("com.android.application") {
            buildNonMkdocsTask.configure { dependsOn(tasks.named("build")) }
        }
        plugins.withId("com.android.library") {
            buildNonMkdocsTask.configure { dependsOn(tasks.named("build")) }
        }
        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            buildNonMkdocsTask.configure { dependsOn(tasks.named("build")) }
        }
        plugins.withId("java-library") {
            buildNonMkdocsTask.configure { dependsOn(tasks.named("build")) }
        }
    } else {
        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            jsBrowserDistributionMkdocsTask
                .configure { dependsOn(tasks.named("jsBrowserDistribution")) }
        }
    }

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

    afterEvaluate {
        // Ensure that all project modules use convention plugins
        if (childProjects.isEmpty()) {
            if (!pluginManager.hasPlugin("com.bumble.appyx.android.application") &&
                !pluginManager.hasPlugin("com.bumble.appyx.android.library") &&
                !pluginManager.hasPlugin("com.bumble.appyx.multiplatform")
            ) {
                error("'$path' module must use a convention plugin")
            }
        }
    }
}
