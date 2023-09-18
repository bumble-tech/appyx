---
title: Appyx – Overview
---

# Appyx

![badge-android](https://img.shields.io/badge/platform-android-brightgreen)
![badge-jvm](https://img.shields.io/badge/platform-jvm-orange)
![badge-macos](https://img.shields.io/badge/platform-macos-purple)
![badge-js](https://img.shields.io/badge/platform-js-yellow)
![badge-ios](https://img.shields.io/badge/platform-ios-lightgray)

Model-driven navigation + UI components with gesture control for Compose Multiplatform.

[https://github.com/bumble-tech/appyx](https://github.com/bumble-tech/appyx)

Find us on Kotlinlang Slack: **#appyx**


## Setup

See [Downloads](releases/downloads.md) and [Navigation quick start guide](navigation/quick-start.md).

## Overview

Appyx is a collection of libraries:

![Overview](/appyx/assets/overview.png)


## Appyx Navigation

**Type-safe navigation for Compose Multiplatform directly from code.**

- Tree-based, composable
- Leverages the transitions and gesture-based capabilities of [Appyx Interactions](interactions/index.md) to build beautiful, custom navigation.
- Use any component for navigation, whether pre-built ([Appyx Components](components/index.md)), or custom-built by you ([Appyx Interactions](interactions/index.md)).

[» More details](navigation/index.md)

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

**Component kit for Compose Multiplatform.**

- Create custom UI components quickly, which can then be used on their own, or inside your navigation tree.
- Animation without writing animation code.
- Gesture control without the usual gesture detection code.

[» More details](interactions/index.md)

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

**Component gallery.** 

Back stack, Spotlight (pager), and other UI components built using [Appyx Interactions](interactions/index.md).

[» More details](components/index.md)

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


## Where to start? 

Depending on what's your use-case with Appyx:

#### Appyx as a navigation solution

Check out [Appyx Navigation](navigation/index.md) and some of the [Appyx Components](components/index.md) you can use in your navigation tree.


#### Creating your own components

Stacks, custom pagers, custom UI components – whether for navigation, or standalone: check out what [Appyx Interactions](interactions/index.md) can do for you.



## 2.x migration guide

If you used Appyx `1.x` before, you can find a [summary of differences](2.x/migrationguide.md) here.


## 1.x documentation

This page is about Appyx `2.x` (alpha).

You can find `1.x` related documentation [here](1.x/index.md).

