# Appyx Components

## Overview

Appyx components come in two major groups: Stable and Experimental.

## Stable

Stable components are ones you can depend on in your project. We maintain them and keep their APIs stable. They might receive non-API breaking updates (e.g. new operations, or additional visualisations).

### Back stack

A standard back stack, with multiple visualisations.

Check [its own page for more details](backstack.md).

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/backstack/fader/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:backstack:fader:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-backstack-fader",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}


## Experimental

Experimental components are still published as artifacts, but meant only for quick prototyping, experimenting, or playing around. There's no promise they will be maintained, or that there API won't change. They might be removed at any point.
