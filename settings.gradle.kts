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
    ":libraries:core",
    ":libraries:customisations",
    ":libraries:dagger-hilt:common",
    ":libraries:dagger-hilt:compiler",
    ":libraries:dagger-hilt:runtime",
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
    ":samples:dagger-hilt:app",
    ":samples:dagger-hilt:library",
    ":samples:navigation-compose",
    ":samples:navmodel-samples",
    ":samples:sandbox",
)

includeBuild("plugins")
