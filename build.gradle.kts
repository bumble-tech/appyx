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

plugins {
    id("appyx-collect-sarif")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

allprojects {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("com.bumble.appyx:customisations"))
                .using(project(":customisations"))
                .because("RIBs uses Appyx customisations as external dependency")
        }
    }
}

subprojects {
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
        val isJavaLibrary = plugins.hasPlugin("java")

        val isAndroidLibrary =
            plugins.hasPlugin("com.android.application") ||
                    plugins.hasPlugin("com.android.library")

        if (isAndroidLibrary || isJavaLibrary) {
            tasks.register("printAllDependencies", DependencyReportTask::class.java) {
                if (isAndroidLibrary) {
                    setConfiguration("releaseRuntimeClasspath")
                } else {
                    setConfiguration("runtimeClasspath")
                }
            }
        }
    }
}
