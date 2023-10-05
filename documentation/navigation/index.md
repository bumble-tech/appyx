---
title: Appyx Navigation – Overview
---

# Appyx Navigation

## Type-safe navigation for Compose directly from code

- Tree-based, composable
- Leverages the transitions and gesture-based capabilities of [Appyx Interactions](../interactions/index.md) to build beautiful, custom navigation.
- Use any component for navigation, whether pre-built ([Appyx Components](../components/index.md)), or custom-built by you ([Appyx Interactions](../interactions/index.md)).

<img src="https://i.imgur.com/hKvOs3w.gif" width="450">


## Quick start

[Quick start guide](quick-start.md)


## Concepts

- [Model-driven navigation](concepts/model-driven-navigation.md)
- [Composable navigation](concepts/composable-navigation.md)
- [Implicit navigation](concepts/implicit-navigation.md)
- [Explicit navigation](concepts/explicit-navigation.md)

## Features

- [ChildAware](features/childaware.md)
- [Deep link navigation](features/deep-linking.md)
- [Lifecycle](features/lifecycle.md)
- [Material 3 support](features/material3.md)
- [Plugins](features/plugins.md)
- [Scoped DI](features/scoped-di.md)
- [Surviving configuration changes](features/surviving-configuration-changes.md)

## Integrations

- [Compose Navigation](integrations/compose-navigation.md)
- [DI frameworks](integrations/di-frameworks.md)
- [RIBs](integrations/ribs.md)
- [RxJava](integrations/rx.md)
- [ViewModel](integrations/viewmodel.md)


## Multiplatform

- [Multiplatform](multiplatform.md)


## Components

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

See [Appyx Components](../components/index.md) for Back stack, Spotlight (pager) and other components you can use in navigation.

See [Appyx Interactions](../interactions/index.md) on how to build your own components with state-based transitions and easy-to-create gesture control.
