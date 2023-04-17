pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        jcenter()
    }
    plugins {
        kotlin("multiplatform").version("1.8.10")
        id("org.jetbrains.compose").version("1.3.1")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://jitpack.io")
    }
}

enableFeaturePreview("VERSION_CATALOGS")

include(
    ":appyx-interactions:android",
    ":appyx-interactions:common",
    ":appyx-interactions:desktop",
    ":appyx-interactions:sample-test-driver",
    ":appyx-navigation",
    ":libraries:customisations",
    ":libraries:interop-ribs",
    ":libraries:interop-rx2",
    ":libraries:interop-rx3",
    ":libraries:testing-junit4",
    ":libraries:testing-junit5",
    ":libraries:testing-ui",
    ":libraries:testing-ui-activity",
    ":libraries:testing-unit-common",
    ":samples:appyx-navigation",
    ":samples:common",
    ":samples:navigation-compose",
)

includeBuild("plugins")
