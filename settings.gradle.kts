pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        jcenter()
    }
    plugins {
        kotlin("multiplatform")
        id("org.jetbrains.compose")
        id("com.google.devtools.ksp")
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
    ":appyx-components:stable:backstack:android",
    ":appyx-components:stable:backstack:common",
    ":appyx-components:demos:android",
    ":appyx-components:demos:common",
    ":appyx-components:demos:web",
    ":appyx-components:internal:test-drive:android",
    ":appyx-components:internal:test-drive:common",
    ":appyx-components:stable:spotlight:android",
    ":appyx-components:stable:spotlight:common",
    ":appyx-interactions:android",
    ":appyx-interactions:desktop",
    ":appyx-interactions:common",
    ":appyx-navigation",
    ":demos:appyx-interactions:android",
    ":demos:appyx-interactions:web",
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

// do not remove this. Otherwise all multiplatform modules will produce clashing artifacts
project(":appyx-components:stable:backstack:common").name = "backstack"
project(":appyx-components:stable:spotlight:common").name = "spotlight"
project(":appyx-components:demos:common").name = "demos"
project(":appyx-interactions:common").name = "appyx-interactions"
project(":appyx-components:demos:web").name = "demos-web"

includeBuild("plugins")
