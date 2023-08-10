---
title: Appyx Navigation – Scoped DI
---

# Scoped DI

Once you represent your navigation in a [Composable way](../concepts/composable-navigation.md), you will get powerful DI scoping as a pleasant side-effect:

<img src="https://i.imgur.com/hKvOs3w.gif" width="450">

Appyx gives every single `Node` its own DI scope for free, with no extra effort required to clean up these scopes other than navigating away from them.


## How does this work?

1. Imagine you create an object in the `Node` related to `Onboarding`, and make it available to all of its children.
2. While navigation is advancing between the individual screens of Onboarding, `O1` – `O2` – `O3`, this object will be the same instance.
3. As soon as the navigation switches to `Main`, the entire subtree of `Onboarding` is destroyed and all held objects are released.
4. Should the navigation ever go back to `Onboarding`, said object would be created from scratch.

This of course applies to every other `Node` in the tree.

## Scoping in practice

Imagine in a larger tree:

<img src="https://i.imgur.com/jkZQJBC.png" width="450">

1. A logout action is represented as switching the navigation back to the `Logged out` node
2. This will destroy the entire `Logged in` scope automatically 
3. All objects held in the scope of an authenticated state are released without any special effort.

You could also use this for DI scoping flows (e.g. a cart object during a multi-screen product checkout). 

With a regular approach these cases could be more difficult to represent:

- Screen-bound scopes wouldn't allow multi-screen lifetime of the objects.
- Application-scoped singletons would require extra attention of cleaning up once the flow ends.

With Appyx you get best of both worlds for free.


