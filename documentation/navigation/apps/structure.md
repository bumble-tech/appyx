# Structuring your app navigation

As seen in [Composable navigation](../navigation/composable-navigation.md), you can make `AppyxComponents` composable. 

To achieve this, Appyx offers the `Node` class as the structural element.


## Node illustration

In many of the examples you'll see this panel as an illustration of a very simple `Node` – it has some local state (id, colour, and a counter).

<img src="https://i.imgur.com/09qR6zl.png" width="150">

If you launch the sample app in the `:app` module, you can also change its state (colour) by tapping it. Its counter is stepped automatically. This is to illustrate that it has its own state, persisted and restored.


## Node overview

You can think of a `Node` as a standalone component with:

- Its own simplified lifecycle
- State restoration
-  A `@Composable` view
- Business logic that's kept alive even when the view isn't added to the composition
- The ability to host generic [Plugins](../apps/plugins.md) to extract extra concerns without enforcing any particular architectural pattern


## Parent nodes, child nodes

`ParentNodes` can have other `Nodes` as children. This means you can represent your whole application as a tree of Appyx nodes.

<img src="https://i.imgur.com/iwSxuZi.png" width="450">

You can go as granular or as high-level as it fits you. This allows to keep the complexity low in individual `Nodes` by extracting responsibilities to children, as well as composing other components to build more complex functionality.


## Composable navigation

<img src="https://i.imgur.com/hKvOs3w.gif" width="450">

`Nodes` offer the structure – `AppyxComponents` add dynamism to it.

Read more in [Composable navigation](../navigation/composable-navigation.md)


## Lifecycle

Nodes have their own lifecycles, directly using the related classes of `androidx.lifecycle`.

Read more in [Lifecycle](../apps/lifecycle.md)


## ChildAware API

React to dynamically added child nodes in the tree: [ChildAware API](childaware.md) 


## Summary

A summary of Appyx's approach to structuring applications:

- Compose your app out of `Nodes` with their own lifecycles and state
- Navigation is local, composed of individual pieces of `AppyxComponents`
- Navigation is stateful
- Navigation is unit-testable
- You're free to implement your own navigable components by utilising `AppyxComponents`
- Avoid global navigation concerns, like shared modules needing to know about the application, or the application needing to know about all its possible modules
