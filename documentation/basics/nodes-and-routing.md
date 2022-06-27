# Nodes and routing

```Nodes``` are the main structural element of an Appyx tree. 

Routing is how we add dynamism to that tree.

## Overview
 
You can think of a `Node` as a standalone component with:

- Their own simplified lifecycle
- State restoration
-  A `@Composable` view
- Business logic that's kept alive even when the view isn't added to the composition
- The ability to host generic [Plugins](plugins.md) to extract extra concerns without enforcing any particular architectural pattern

## Node illustration 

In many of the examples you'll see this panel as an illustration of a very simple `Node` – it has some local state (id, colour, and a counter).

<img src="https://i.imgur.com/09qR6zl.png" width="150">

If you launch `:app` you can also change its state (colour) by tapping it. Its counter is stepped automatically. All of its state is persisted and restored.
   
## Parent nodes, child nodes

`Nodes` are composable, as `ParentNodes` can have other `Nodes` as children. This means you can represent your whole application as a tree of Appyx nodes.

<img src="https://i.imgur.com/iwSxuZi.png" width="450">

This allows to keep the complexity low in individual `Nodes` by extracting responsibilities to children, as well as composing other components to build more complex functionality. 


## Dynamism

Having a static composition of `Nodes` isn't very exciting. Based on the changes of business logic you'll want to:

- Add or remove child `Nodes` of a `ParentNode`
- Move them on and off the screen
- Change their states

<img src="https://i.imgur.com/8gy3Ghb.gif" width="200"> <img src="https://i.imgur.com/N8rEPrJ.gif" width="200">

The back stack illustrates adding and removing child `Nodes` as well as moving them on and off the screen.
Tiles illustrates changing the state of children and removing them from the `ParentNode`.


## Routing: local bits of navigation

All `ParentNodes` have the option to achieve this dynamism by utilising [Routing sources](../routingsources/index.md) such as the back stack. Put simply:

- A routing is a relation to a child `Node`
- Since `Nodes` are composed and routing exist on every level, the sum total of those relations define which components are active and what part of the application the user sees
- A routing change will look like a piece of navigation happening to the user of the app

## Benefits of routing

Following from the above:

- Navigation is broken down to individual pieces of routing
- Routing, as this piece of navigation is now the responsibility of the individual `ParentNode`
- Navigation is now business-logic driven
- Navigation is now unit-testable
- You can avoid global navigation concerns, like shared modules needing to know about the application, or the application needing to know about all its possible modules
