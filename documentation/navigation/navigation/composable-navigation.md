# Composable navigation

[AppyxComponents](../../components/index.md) in Appyx are composable. 

As a single `AppyxComponent` won't be enough for the whole of your whole app, you can use many in a composable way. That is, any managed element of a `AppyxComponent` can also host its own `AppyxComponent`.


## Structural element for composing navigation

```Nodes``` are the main structural element in Appyx. They can host `AppyxComponents`, and they form a tree.

This allows you to make your app's business logic also composable by leveraging `Nodes` as lifecycled components.

Read more in [Structuring your app navigation](../apps/structure.md)


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
