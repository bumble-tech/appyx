---
title: Appyx + DI frameworks
---

# Appyx + DI frameworks

## Appyx + Hilt

We've experimented with adding Hilt support for Appyx Nodes, however the solution is not yet finalised.

You can track and vote on the issue here:

- [#552](https://github.com/bumble-tech/appyx/issues/552) - Integration: Appyx + Hilt


## Appyx + Kodein

If you'd like us to prioritise it, you can vote on the issue here:

- [#555](https://github.com/bumble-tech/appyx/issues/555) - Integration: Appyx + Kodein


## Appyx + Koin

If you'd like us to prioritise it, you can vote on the issue here:

- [#554](https://github.com/bumble-tech/appyx/issues/554) - Integration: Appyx + Koin


## Appyx + Dagger

If you're not too bothered to create the extra classes for Dagger yourself, you can make it work pretty nicely with a tree-based approach like Appyx. We have detailed this in this project's predecessor, [badoo/RIBs](https://github.com/badoo/RIBs/blob/master/documentation/tree-structure-101/providing-dependencies.md#dagger) – however we moved away from it and didn't implement a sample in the scope Appyx. 


## Appyx + Manual DI

It's worth mentioning that while manual DI in an unstructured project sounds like a bad idea, with a tree-scoped project structure it can be viable. 

Benefits:

- [Scoped DI for free](../features/scoped-di.md).
- The tree provides a good enough structure to make it understandable.
- Less boilerplate than with Dagger.
