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
    ":app",
    ":core",
    ":sandbox",
    ":interop-rx2",
    ":interop-ribs",
    ":testing-junit4",
    ":testing-junit5",
    ":testing-ui",
    ":testing-unit-common",
    ":routing-source-addons",
    ":customisations",
    ":samples:navigation-compose",
)

includeBuild("plugins")
