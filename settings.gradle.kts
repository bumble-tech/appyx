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
    ":appyx-components:stable:spotlight:android",
    ":appyx-components:stable:spotlight:common",
    ":appyx-components:internal:test-drive:android",
    ":appyx-components:internal:test-drive:common",
    ":appyx-components:experimental:cards:android",
    ":appyx-components:experimental:cards:common",
    ":appyx-components:experimental:promoter:android",
    ":appyx-components:experimental:promoter:common",
    ":appyx-components:experimental:puzzle15:android",
    ":appyx-components:experimental:puzzle15:common",
    ":appyx-components:experimental:puzzle15:web",
    ":appyx-interactions:android",
    ":appyx-interactions:common",
    ":appyx-navigation:android",
    ":appyx-navigation:desktop",
    ":appyx-navigation:web",
    ":appyx-navigation:common",
    ":demos:appyx-interactions:android",
    ":demos:appyx-interactions:desktop",
    ":demos:appyx-interactions:web",
    ":demos:appyx-navigation:common",
    ":demos:appyx-navigation:android",
    ":demos:appyx-navigation:desktop",
    ":demos:appyx-navigation:web",
    ":demos:common",
    ":demos:navigation-compose",
    ":demos:mkdocs:appyx-interactions:interactions:sample1:web",
    ":demos:mkdocs:appyx-interactions:interactions:sample2:web",
    ":demos:mkdocs:appyx-interactions:interactions:sample3:web",
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
    ":utils:multiplatform",
)

// do not remove this. Otherwise all multiplatform modules will produce clashing artifacts
project(":appyx-components:stable:backstack:common").name = "backstack"
project(":appyx-components:stable:spotlight:common").name = "spotlight"
project(":appyx-interactions:common").name = "appyx-interactions"
project(":appyx-components:experimental:cards:common").name = "cards"
project(":appyx-components:experimental:promoter:common").name = "promoter"
project(":appyx-components:experimental:puzzle15:common").name = "puzzle15"
project(":appyx-components:experimental:puzzle15:web").name = "puzzle15-web"
project(":appyx-components:internal:test-drive:common").name = "test-drive"
project(":demos:appyx-navigation:web").name = "navigation-web"

includeBuild("plugins")
