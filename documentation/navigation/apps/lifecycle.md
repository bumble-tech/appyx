# Lifecycle

Nodes have their own lifecycles, directly using the related classes of `androidx.lifecycle`.

## Capping

No node can be in a higher lifecycle state than any of its parents or the Android Activity it lives in.

## On-screen & off-screen

Appyx controls which children of an `AppyxComponent` should be added to the composition and which should not, based on their state and properties. This is done automatically.


## Lifecycle changes

Child elements automatically receive appropriate lifecycle callbacks. The lifecycle state can be affected by:

- The `AppyxComponent` of the parent (adding or removing child `Nodes` and changing their on-screen status)
- The parent's lifecycle state capping its children (transitive in the tree)
- Android lifecycle (Activity) capping the whole tree


## Back stack node lifecycle

An example demonstrating the above:

<img src="https://i.imgur.com/WlcQHqV.gif" width="200">

Note that individual `AppyxComponents` might have their slight differences (e.g. whether their operations remove a `Node` only from the view, or completely destroy it).

In the case of the back stack:

- The `Push` operation adds a new element and stashes the currently active one – the stashed one will be removed from the view & `STOPPED`
- The `Pop` operation removes an element, the child `Node` will be `DESTROYED`
