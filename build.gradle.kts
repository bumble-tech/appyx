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
