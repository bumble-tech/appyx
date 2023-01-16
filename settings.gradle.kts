pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

enableFeaturePreview("VERSION_CATALOGS")

include(
    ":appyx-interactions",
    ":libraries:core",
    ":libraries:customisations",
    ":libraries:interop-ribs",
    ":libraries:interop-rx2",
    ":libraries:interop-rx3",
    ":libraries:testing-junit4",
    ":libraries:testing-junit5",
    ":libraries:testing-ui",
    ":libraries:testing-ui-activity",
    ":libraries:testing-unit-common",
    ":samples:app",
    ":samples:common",
    ":samples:navigation-compose",
    ":samples:navmodel-samples",
    ":samples:sandbox",
    ":samples:sandbox2",
)

includeBuild("plugins")
