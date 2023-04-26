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
        id("com.google.devtools.ksp") version("1.8.10-1.0.9") apply false
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
    ":appyx-components:backstack:android",
    ":appyx-components:backstack:common",
    ":appyx-components:demos:android",
    ":appyx-components:demos:common",
    ":appyx-components:internal:android",
    ":appyx-components:internal:common",
    ":appyx-components:spotlight:android",
    ":appyx-components:spotlight:common",
    ":appyx-interactions:android",
    ":appyx-interactions:desktop",
    ":appyx-interactions:common",
    ":appyx-navigation",
    ":demos:appyx-interactions:android",
//    ":demos:appyx-interactions:web",
    ":demos:appyx-navigation",
    ":demos:common",
    ":demos:navigation-compose",
    ":ksp:mutable-ui-processor",
    ":utils:customisations",
    ":utils:interop-ribs",
    ":utils:interop-rx2",
    ":utils:interop-rx3",
    ":utils:testing-junit4",
    ":utils:testing-junit5",
    ":utils:testing-ui",
    ":utils:testing-ui-activity",
    ":utils:testing-unit-common",
)

includeBuild("plugins")
