{% include-markdown "../deprecation.md" %}

# Lifecycle

Nodes have their own lifecycles, directly using the related classes of `androidx.lifecycle`.

## Capping

No node can be in a higher lifecycle state than any of its parents or the Android Activity it lives in.

## On-screen & off-screen

`NavModel` controls which children should be rendered on the screen and which should not with `NavModel.screenState`.
The behaviour is customisable in `BaseNavModel` via `OnScreenStateResolver`.

When a `NavElement` of the node is marked as on-screen, its lifecycle follows the parent node's lifecycle.
The rendering status does not affect it – the node might not be added to Compose view and still be in a `RESUMED` state.

When a `NavElement` of the node is marked as off-screen, the following might happen:

- Its lifecycle is capped with `CREATED` (or `STOPPED`) in case of `ChildEntry.KeepMode.KEEP`. 
- The node is destroyed and its state is saved in case of `ChildEntry.KeepMode.SUSPEND`.

`ChildEntry.KeepMode` settings can be configured for each `ParentNode` individually or globally via `Appyx.defaultChildKeepMode`.

When a node is removed completely from `NavModel`, it will be in `DESTROYED` state.

## Lifecycle changes

The lifecycle state can be affected by:

- The NavModel of the parent (adding or removing child `Nodes` and changing their on-screen status)
- The parent's lifecycle state capping its children (transitive in the tree)
- Android lifecycle (Activity) capping the whole tree

## Back stack node lifecycle

An example demonstrating the above:

<img src="https://i.imgur.com/WlcQHqV.gif" width="200">

Note that NavModels might have their slight differences (e.g. whether their operations remove a `Node` only from the view, or completely destroy it).

In the case of the back stack:

- The `Push` operation adds a new element and stashes the currently active one – the stashed one will be removed from the view & `STOPPED`
- The `Pop` operation removes an element, the child `Node` will be `DESTROYED`
