# Nodes and routing

```Nodes``` are the main structural element of an Appyx tree. 

Routing is how we add dynamism to that tree.

## Overview
 
You can think of a `Node` as a standalone component with:

- Their own simplified lifecycle
- State restoration
-  A `@Composable` view
- Business logic that's kept alive even when the view isn't added to the composition
- The ability to host generic [Plugins](../other/plugins.md) to extract extra concerns without enforcing any particular architectural pattern

## Node illustration 

In many of the examples you'll see this panel as an illustration of a very simple `Node` – it has some local state (id, colour, and a counter).

<img src="https://i.imgur.com/09qR6zl.png" width="150">

If you launch `:app` you can also change its state (colour) by tapping it. Its counter is stepped automatically. All of its state is persisted and restored.
   
## Parent nodes, child nodes

`Nodes` are composable, as `ParentNodes` can have other `Nodes` as children. This means you can represent your whole application as a tree of Appyx nodes.

<img src="https://i.imgur.com/iwSxuZi.png" width="450">

You can go as granular or as high-level as it fits you. This allows to keep the complexity low in individual `Nodes` by extracting responsibilities to children, as well as composing other components to build more complex functionality. 


## Dynamism by routing

<img src="https://i.imgur.com/hKvOs3w.gif" width="450">

Having a static composition of `Nodes` isn't very exciting. You can add dynamism to the tree by changing the control flow via routing:

- A routing is a relation of a parent `Node` to a child `Node`
- The sum total of those relations in the app defines what part of the application the user sees
- A routing change will look and feel like navigation to the user of the app


## Routing sources

All `ParentNodes` have the option to achieve this dynamism by utilising [Routing sources](../routingsources/index.md) such as the back stack. Using them you can:

- Add or remove child `Nodes` of a `ParentNode`
- Move them on and off the screen
- Change their states

<img src="https://i.imgur.com/8gy3Ghb.gif" width="200"> <img src="https://i.imgur.com/N8rEPrJ.gif" width="200">

Here:

- `Back stack` illustrates adding and removing child `Nodes`
- `Tiles` illustrates changing the state of children and removing them from the `ParentNode`

These are just two examples, you're of course not limited to using them.


## Summary

A summary of Appyx's approach to apps:

- Compose your app out of components with their own lifecycles and state
- Navigation is local, broken down to individual pieces of routing
- Navigation is business-logic driven
- Navigation is stateful
- Navigation is unit-testable
- You're free to implement your own navigation engines by utilising routing sources
- Avoid global navigation concerns, like shared modules needing to know about the application, or the application needing to know about all its possible modules
