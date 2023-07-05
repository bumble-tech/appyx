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
    ":appyx-components:experimental:modal:android",
    ":appyx-components:experimental:modal:common",
    ":appyx-components:experimental:promoter:android",
    ":appyx-components:experimental:promoter:common",
    ":appyx-components:experimental:puzzle15:android",
    ":appyx-components:experimental:puzzle15:common",
    ":appyx-components:experimental:puzzle15:web",
    ":appyx-interactions:android",
    ":appyx-interactions:common",
    ":appyx-navigation",
    ":demos:appyx-interactions:android",
    ":demos:appyx-interactions:desktop",
    ":demos:appyx-interactions:web",
    ":demos:appyx-navigation",
    ":demos:common",
    ":demos:navigation-compose",
    ":demos:mkdocs:appyx-interactions:interactions:sample1:web",
    ":demos:mkdocs:appyx-interactions:interactions:sample2:web",
    ":demos:mkdocs:appyx-interactions:interactions:sample3:web",
    ":demos:mkdocs:appyx-interactions:gestures:dragprediction:web",
    ":demos:mkdocs:appyx-interactions:gestures:incompletedrag:web",
    ":demos:mkdocs:appyx-components:backstack:fader:web",
    ":demos:mkdocs:appyx-components:backstack:parallax:web",
    ":demos:mkdocs:appyx-components:backstack:slider:web",
    ":demos:mkdocs:appyx-components:backstack:stack3d:web",
    ":demos:mkdocs:appyx-components:common",
    ":demos:mkdocs:appyx-components:experimental:datingcards:web",
    ":demos:mkdocs:appyx-components:experimental:puzzle15:web",
    ":demos:mkdocs:appyx-components:spotlight:fader:web",
    ":demos:mkdocs:appyx-components:spotlight:slider:web",
    ":demos:mkdocs:appyx-components:spotlight:sliderrotation:web",
    ":demos:mkdocs:appyx-components:spotlight:sliderscale:web",
    ":demos:mkdocs:appyx-components:spotlight:stack3d:web",
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
project(":appyx-components:experimental:modal:common").name = "modal"
project(":appyx-components:stable:spotlight:common").name = "spotlight"
project(":appyx-interactions:common").name = "appyx-interactions"
project(":appyx-components:experimental:cards:common").name = "cards"
project(":appyx-components:experimental:promoter:common").name = "promoter"
project(":appyx-components:experimental:puzzle15:common").name = "puzzle15"
project(":appyx-components:experimental:puzzle15:web").name = "puzzle15-web"
project(":appyx-components:internal:test-drive:common").name = "test-drive"

includeBuild("plugins")
