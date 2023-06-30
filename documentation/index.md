# Appyx


Transitions, gestures, navigation for Jetpack Compose

[https://github.com/bumble-tech/appyx](https://github.com/bumble-tech/appyx)


## Appyx Interactions

[A gesture-driven component kit for Compose Multiplatform.](interactions/index.md)

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-interactions/interactions/sample2/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-interactions:interactions:sample2:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-interactions-index-2",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}


## Appyx Components

[Back stack, Spotlight (pager), and other UI components built using Appyx Interactions.](components/index.md)

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/spotlight/slider/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:spotlight:slider:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-spotlight-slider",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

## Appyx Navigation

An Android navigation library for Jetpack Compose that depends on **Appyx Interactions** for its transitions and gesture-based navigation. It allows you to use any **Appyx Components** for navigation.

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/backstack/parallax/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:backstack:parallax:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-backstack-parallax",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}


## 2.x migration guide

If you used Appyx 1.x before, you can find a [summary of differences](2.x/migrationguide.md) here.


## 1.x documentation

This page is about Appyx 2.x (alpha).

You can find 1.x related documentation [here](1.x/index.md)

