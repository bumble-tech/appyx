pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    plugins {
        kotlin("multiplatform")
        id("org.jetbrains.compose")
        id("com.google.devtools.ksp")
        id("com.android.test")
    }
    includeBuild("plugins")
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

include(
    ":appyx-components:stable:backstack:android",
    ":appyx-components:stable:backstack:common",
    ":appyx-components:stable:spotlight:android",
    ":appyx-components:stable:spotlight:common",
    ":appyx-components:internal:test-drive:android",
    ":appyx-components:internal:test-drive:common",
    ":appyx-components:experimental:cards:android",
    ":appyx-components:experimental:cards:common",
    ":appyx-components:experimental:modal:common",
    ":appyx-components:experimental:promoter:android",
    ":appyx-components:experimental:promoter:common",
    ":appyx-components:experimental:puzzle15:android",
    ":appyx-components:experimental:puzzle15:common",
    ":appyx-components:experimental:puzzle15:web",
    ":appyx-interactions:android",
    ":appyx-interactions:common",
    ":appyx-navigation:android",
    ":appyx-navigation:common",
    ":benchmark:benchmark-app",
    ":benchmark:benchmark-test",
    ":demos:appyx-interactions:android",
    ":demos:appyx-interactions:desktop",
    ":demos:appyx-interactions:ios",
    ":demos:appyx-interactions:web",
    ":demos:appyx-navigation:common",
    ":demos:appyx-navigation:android",
    ":demos:appyx-navigation:desktop",
    ":demos:appyx-navigation:ios",
    ":demos:appyx-navigation:web",
    ":demos:common",
    ":demos:image-loader:common",
    ":demos:navigation-compose",
    ":demos:mkdocs:appyx-interactions:interactions:sample1:web",
    ":demos:mkdocs:appyx-interactions:interactions:sample2:web",
    ":demos:mkdocs:appyx-interactions:interactions:sample3:web",
    ":demos:mkdocs:appyx-interactions:interactions:observemp:web",
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
    ":demos:mkdocs:common",
    ":demos:sandbox-appyx-navigation:common",
    ":demos:sandbox-appyx-navigation:android",
    ":demos:sandbox-appyx-navigation:desktop",
    ":demos:sandbox-appyx-navigation:ios",
    ":demos:sandbox-appyx-navigation:web",
    ":ksp:mutable-ui-processor",
    ":utils:customisations",
    ":utils:interop-coroutines",
    ":utils:interop-ribs",
    ":utils:interop-rx2",
    ":utils:interop-rx3",
    ":utils:testing-junit4",
    ":utils:testing-junit5",
    ":utils:testing-ui",
    ":utils:testing-ui-activity",
    ":utils:testing-unit-common",
    ":utils:material3",
    ":utils:multiplatform",
)

// do not remove this. Otherwise all multiplatform modules will produce clashing artifacts
project(":appyx-interactions:common").name = "appyx-interactions"
project(":appyx-navigation:common").name = "appyx-navigation"
project(":appyx-components:stable:backstack:common").name = "backstack"
project(":appyx-components:stable:spotlight:common").name = "spotlight"
project(":appyx-components:experimental:cards:common").name = "cards"
project(":appyx-components:experimental:modal:common").name = "modal"
project(":appyx-components:experimental:promoter:common").name = "promoter"
project(":appyx-components:experimental:puzzle15:common").name = "puzzle15"
project(":appyx-components:experimental:puzzle15:web").name = "puzzle15-web"
project(":appyx-components:internal:test-drive:common").name = "test-drive"
project(":demos:image-loader:common").name = "image-loader"
project(":demos:sandbox-appyx-navigation:web").name = "navigation-web"
project(":utils:customisations").name = "utils-customisations"
project(":utils:material3").name = "utils-material3"
project(":utils:multiplatform").name = "utils-multiplatform"
project(":ksp:mutable-ui-processor").name = "appyx-processor"
