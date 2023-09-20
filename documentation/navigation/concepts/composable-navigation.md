---
title: Appyx Navigation – Composable navigation 
---

# Composable navigation

[AppyxComponents](../../components/index.md) in Appyx are composable. 

As a single `AppyxComponent` won't be enough for the whole of your whole app, you can use many in a composable way. That is, any managed element of a `AppyxComponent` can also host its own `AppyxComponent`.


## Nodes – structural elements for composing navigation

```Nodes``` are the main structural element in Appyx. They can form a tree.

You can think of a `Node` as a standalone unit of your app with:

- Its own `AppyxComponent`
- Its own [Lifecycle](../features/lifecycle.md)
- State restoration
-  A `@Composable` view
- Business logic that's kept alive even when the view isn't added to the composition
- The ability to host generic [Plugins](../features/plugins.md) to extract extra concerns without enforcing any particular architectural pattern

This allows you to make your app's business logic also composable by leveraging `Nodes` as lifecycled components.


## Parent nodes, child nodes

`ParentNodes` can have other `Nodes` as children. This means you can represent your whole application as a tree of Appyx nodes.

<img src="https://i.imgur.com/iwSxuZi.png" width="450">

You can go as granular or as high-level as it fits you. This allows to keep the complexity low in individual `Nodes` by extracting responsibilities to children, as well as composing other components to build more complex functionality.


## ChildAware API

A `ParentNode` can react to dynamically added child `Nodes` in the tree: [ChildAware API](../features/childaware.md)


## Navigation in the tree

<img src="https://i.imgur.com/hKvOs3w.gif" width="450">

Once you've structured your navigation in a composable way, you can add an `AppyxComponent` to a `Node` of this tree and make it dynamic:

- Some parts in this tree are active while others ore not
- The active parts define what state the application is in, and what the user sees on the screen
- We can change what's active by using an `AppyxComponent` on each level of the tree
- Changes will feel like navigation to the user

See [Implicit navigation](implicit-navigation.md) and [Explicit navigation](explicit-navigation.md) for building complex navigation behaviours with this approach.



## How AppyxComponents affect Nodes

`AppyxComponent` operations will typically result in:

- Adding or removing child `Nodes` of a `ParentNode`
- Move them on and off the screen
- Change their states

As an illustration:

<img src="https://i.imgur.com/8gy3Ghb.gif" width="200"> <img src="https://i.imgur.com/N8rEPrJ.gif" width="200">

Here:

- `Back stack` illustrates adding and removing child `Nodes`
- `Tiles` illustrates changing the state of children and removing them from the `ParentNode`

These are just two examples, you're of course not limited to using them.



## Summary

A summary of Appyx's approach to structuring applications:

- Compose your app out of `Nodes` with their own lifecycles and state
- Navigation is local, composed of individual pieces of `AppyxComponents`
- Navigation is stateful
- Navigation is unit-testable
- Nested navigation and multi-module navigation works as a default
- You're free to implement your own navigable components by utilising `AppyxComponents`
- Avoid global navigation concerns, like shared modules needing to know about the application, or the application needing to know about all its possible modules
