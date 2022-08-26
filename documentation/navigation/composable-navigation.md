# Composable navigation

[NavModels](../navmodel/index.md) in Appyx are composable. 

As a single `NavModel` won't be enough for the whole of your whole app, you can use many in a composable way. That is, any navigation target of a `NavModel` can also host its own `NavModel`.


## Structural element for composing navigation

```Nodes``` are the main structural element in Appyx. They can host `NavModels`, and they form a tree.

This allows you to make your app's business logic also composable by leveraging `Nodes` as lifecycled components.


## Node overview
 
You can think of a `Node` as a standalone component with:

- Their own simplified lifecycle
- State restoration
-  A `@Composable` view
- Business logic that's kept alive even when the view isn't added to the composition
- The ability to host generic [Plugins](../apps/plugins.md) to extract extra concerns without enforcing any particular architectural pattern


## Node illustration 

In many of the examples you'll see this panel as an illustration of a very simple `Node` – it has some local state (id, colour, and a counter).

<img src="https://i.imgur.com/09qR6zl.png" width="150">

If you launch `:app` you can also change its state (colour) by tapping it. Its counter is stepped automatically. All of its state is persisted and restored.
   

## Parent nodes, child nodes

`ParentNodes` can have other `Nodes` as children. This means you can represent your whole application as a tree of Appyx nodes.

<img src="https://i.imgur.com/iwSxuZi.png" width="450">

You can go as granular or as high-level as it fits you. This allows to keep the complexity low in individual `Nodes` by extracting responsibilities to children, as well as composing other components to build more complex functionality. 


## Navigation in the tree

<img src="https://i.imgur.com/hKvOs3w.gif" width="450">

Having a static composition of `Nodes` isn't very exciting. So rather:

- Some parts in this tree are active while others ore not
- The activate parts define what state the application is in, and what the user sees on the screen
- We can change what's active by using `NavModels` on each level of the tree
- Changes will feel like navigation to the user


## Navigation model

All `ParentNodes` have the option to achieve this dynamism by utilising [NavModels](../navmodel/index.md) such as the back stack. Using them you can:

- Add or remove child `Nodes` of a `ParentNode`
- Move them on and off the screen
- Change their states

<img src="https://i.imgur.com/8gy3Ghb.gif" width="200"> <img src="https://i.imgur.com/N8rEPrJ.gif" width="200">

Here:

- `Back stack` illustrates adding and removing child `Nodes`
- `Tiles` illustrates changing the state of children and removing them from the `ParentNode`

These are just two examples, you're of course not limited to using them.


## Lifecycle

Nodes have their own lifecycles, directly using the related classes of `androidx.lifecycle`.

Read more in [Lifecycle](../apps/lifecycle.md)


## Summary

A summary of Appyx's approach to structuring applications:

- Compose your app out of `Nodes` with their own lifecycles and state
- Navigation is local, composed of individual pieces of `NavModels` 
- Navigation is stateful
- Navigation is unit-testable
- You're free to implement your own navigable components by utilising `NavModels`
- Avoid global navigation concerns, like shared modules needing to know about the application, or the application needing to know about all its possible modules
