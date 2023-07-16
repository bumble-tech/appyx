# Appyx


Model-driven navigation + UI components with gesture control for Compose Multiplatform.

[https://github.com/bumble-tech/appyx](https://github.com/bumble-tech/appyx)

Find us on Kotlinlang Slack: **#appyx**


## Overview

Appyx is a collection of libraries:

![Overview](/appyx/assets/overview.png)

**Appyx as a navigation solution**: check out [Appyx Navigation](navigation/index.md) + some of the [Appyx Components](components/index.md) you can use in your navigation tree.

**Creating your own components** (whether for navigation, or standalone): check out [Appyx Interactions](interactions/index.md).


## Appyx Navigation

[Composable, type-safe navigation directly from code.](navigation/index.md)

- Leverages the transitions and gesture-based capabilities to **Appyx Interactions** to build beautiful, custom navigation.
- Use any component for navigation, whether pre-built (see: [Appyx Components](components/index.md)), or custom-built by you (see: [Appyx Interactions](interactions/index.md)).


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

## Appyx Interactions

[Component kit for Compose Multiplatform.](interactions/index.md)

- Create custom UI components quickly, which can then be used on their own, or inside your navigation tree.
- Animation without writing animation code.
- Gesture control without the usual gesture detection code.

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

[Component gallery.](components/index.md)

Back stack, Spotlight (pager), and other UI components built using Appyx Interactions.

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



## 2.x migration guide

If you used Appyx `1.x` before, you can find a [summary of differences](2.x/migrationguide.md) here.


## 1.x documentation

This page is about Appyx `2.x` (alpha).

You can find `1.x` related documentation [here](1.x/index.md).

