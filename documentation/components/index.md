# Appyx Components

## Overview

With Appyx, you can:

- Create your own components using [Appyx interactions](../interactions/appyxcomponent.md), or
- You can use the ones published by us.  

This section focuses on the latter. Published components come in two major groups: Stable and Experimental.


## Stable

Stable components are ones you can depend on in your project. We maintain them and keep their APIs stable. They might receive non-API breaking updates (e.g. new operations, or additional visualisations).


### Back stack

A standard back stack, with multiple visualisations.

Check [its own page for more details](backstack.md).

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

### Spotlight

A view pager-like component, with multiple visualisations.

Check [its own page for more details](spotlight.md).

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


## Experimental

Experimental components are still published as artifacts, but meant only for quick prototyping, experimenting, or playing around. There's no promise they will be maintained, or that their API won't change. They might be removed at any point.

Check [its own page for more details](experimental.md).

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-components/experimental/puzzle15/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-components:experimental:puzzle15:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-components-experimental-puzzle15",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}
