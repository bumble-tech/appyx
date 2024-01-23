---
title: Appyx Navigation – Quick start guide
---

# Quick start guide

You can check out [Composable navigation](concepts/composable-navigation.md), which explains the concepts you'll encounter in this guide.


## The scope of this guide

The steps below will cover:

1. Integrating Appyx into your project
2. Creating a very simple `Node` hierarchy
3. We'll use a simple back stack for navigation
4. We'll see how to change transition animations easily

This should be enough to get you started as a rudimentary application structure.


## 1. Add Appyx to your project

Please refer to the [Downloads](../releases/downloads.md) to find the relevant artifacts
(also depending on whether you're doing this in a multiplatform project or not).

For the scope of this quick start guide, you will need to add dependencies for:

- [Appyx Navigation](../releases/downloads.md#appyx-navigation)
- [Back stack](../releases/downloads.md#back-stack)


## 2. Create a root Node

```kotlin
class RootNode(
    nodeContext: NodeContext
) : LeafNode(
    nodeContext = nodeContext
) {
    @Composable
    override fun Content(modifier: Modifier) {
        Text("Hello world!")
    }
}
```

## 3. Connect to your platform

Plug your root node into your platform: [Multiplatform | Node hosts](multiplatform.md#node-hosts).

Use the multiplatform imports Appyx provides in any of the code snippets from now on, such as: 

```kotlin
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue
```

## 4. Define children

A single leaf node isn't all that interesting. Let's add some children to the root!

First, let's define the possible set of children using a sealed class. We'll refer them via these navigation targets:

```kotlin
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

/**
 * You can create this class inside the body of RootNode
 * 
 * Note: You must apply the 'kotlin-parcelize' plugin to use @Parcelize
 * https://developer.android.com/kotlin/parcelize
 */
sealed class NavTarget : Parcelable {
    @Parcelize
    object Child1 : NavTarget()

    @Parcelize
    object Child2 : NavTarget()

    @Parcelize
    object Child3 : NavTarget()
}
```

Next, let's modify `RootNode` so it extends `Node` instead of `LeafNode`:

```kotlin
class RootNode(
    nodeContext: NodeContext
) : Node<NavTarget>(
    appyxComponent = TODO("We will come back to this in Step 5"),
    nodeContext = nodeContext
) {
```

`Node` expects us to implement the abstract method `buildChildNode`. This is how we relate navigation targets to actual children. Let's use these helper methods to define some placeholders for the time being – we'll soon make them more appealing:

```kotlin
override fun buildChildNode(reference: NavTarget, nodeContext: NodeContext): Node =
    when (reference) {
        NavTarget.Child1 -> node(nodeContext) { Text(text = "Placeholder for child 1") }
        NavTarget.Child2 -> node(nodeContext) { Text(text = "Placeholder for child 2") } 
        NavTarget.Child3 -> node(nodeContext) { Text(text = "Placeholder for child 3") }
    }
```

Great! With this mapping created, we can now just refer to children using the sealed class elements, and Appyx will be able to relate them to other nodes.

## 5. Add a back stack

The project wouldn't compile just yet. `Node` expects us to pass an instance of an `AppyxComponent` – the main control structure in any case when we want to add children. No need to worry now – for simplicity, let's just go with a simple `BackStack` implementation here:

```kotlin
class RootNode(
    nodeContext: NodeContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTarget = NavTarget.Child1,
            savedStateMap = nodeContext.savedStateMap,        
        ),
        visualisation = { BackStackFader(it) }
    )
) : Node<NavTarget>(
    nodeContext = nodeContext,
    appyxComponent = backStack // pass it here
) {
```

With this simple addition we've immediately gained a lot of power! Now we can use the back stack's API to add, replace, pop children with operations like:

```kotlin
backStack.push(NavTarget.Child2)    // will add a new navigation target to the end of the stack and make it active 
backStack.replace(NavTarget.Child3) // will replace the currently active child
backStack.pop()                     // will remove the currently active child and restore the one before it
```

Since we passed the back stack to the `Node`, all such changes will be immediately reflected. We only need to add it to the composition:

```kotlin
@Composable
override fun Content(modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        // Let's include the elements of our component into the composition
        AppyxNavigationContainer(
            appyxComponent = backStack,
            modifier = Modifier.weight(0.9f)
        )
        
        // Let's also add some controls so we can test it
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
        ) {
            TextButton(onClick = { backStack.push(NavTarget.Child1) }) {
                Text(text = "Push child 1")
            }
            TextButton(onClick = { backStack.push(NavTarget.Child2) }) {
                Text(text = "Push child 2")
            }
            TextButton(onClick = { backStack.push(NavTarget.Child3) }) {
                Text(text = "Push child 3")
            }
            TextButton(onClick = { backStack.pop() }) {
                Text(text = "Pop")
            }
        }
    }
}
```

## 6. Visualisations, transitions

Adding a different visual representation of our model and its state changes is as simple as changing this:

```kotlin
visualisation = { BackStackFader(it) }
```

to this:

```kotlin
visualisation = { BackStackSlider(it) }
```

or this:

```kotlin
visualisation = { BackStackParallax(it) }
```

or this:

```kotlin
visualisation = { BackStack3D(it) }
```

Be sure to check the [Back stack documentation](../components/backstack.md) where you can also find live previews of the above visualisations.

Need something more custom?

1. You can [create your own visualisations](../interactions/ui-representation.md).
2. Instead of a back stack, you can also find other [Components](../components/index.md) in the library, or you can [create your own](../interactions/appyxcomponent.md).


## 7. Proper child nodes  

As a last step, let's replace at least one of the child placeholders with another proper node.

Let's create a dedicated class:

```kotlin
class SomeChildNode(
    nodeContext: NodeContext
) : Node(
    nodeContext = nodeContext
) {
    @Composable
    override fun Content(modifier: Modifier) {
        Text("This is SomeChildNode")
    }
}
```

Now we can update the `buildChildNode` method in `RootNode` so that the target `Child3` refers to this node. It should work out of the box:

```kotlin
override fun buildChildNode(reference: NavTarget, nodeContext: NodeContext): Node =
    when (reference) {
        NavTarget.Child1 -> node(nodeContext) { Text(text = "Placeholder for child 1") }
        NavTarget.Child2 -> node(nodeContext) { Text(text = "Placeholder for child 2") } 
        NavTarget.Child3 -> SomeChildNode(nodeContext)
    }
```

## What's next?

Congrats, you've just built your first Appyx tree!

You can repeat the same pattern and make any embedded children also a `Node` with their own children, navigation models, and transitions. As complexity grows, generally you would:

1. Have a `LeafNode`
2. At some point make it a `Node` and add children to it
3. At some point extract the increasing complexity from a placeholder to another `LeafNode` 
4. Repeat the same on children, go to `1.`


## Further reading

- Check out [Model-driven navigation](concepts/model-driven-navigation.md) how to take your navigation to the next level
- You can (and probably should) also extract local business logic, the view, any any other components into separate classes and [Plugins](features/plugins.md).
