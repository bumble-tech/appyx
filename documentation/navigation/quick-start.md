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

You can find the related Gradle dependencies in [Downloads](../releases/downloads.md).


## 2. Create a root Node

```kotlin
class RootNode(
    buildContext: BuildContext
) : Node(
    buildContext = buildContext
) {
    @Composable
    override fun View(modifier: Modifier) {
        Text("Hello world!")
    }
}
```

Since this is the root of your tree, you'll also need to plug it in to your Activity, so that system events (lifecycle, back press, etc.) reach your components in the tree.

```kotlin
// Please note we are extending NodeActivity
class MainActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                NodeHost(integrationPoint = appyxIntegrationPoint) {
                    RootNode(buildContext = it)
                }
            }
        }
    }
}
```

You only need to do this for the root of the tree.


## 3. Define children

A single leaf node isn't all that interesting. Let's add some children to the root!

First, let's define the possible set of children using a sealed class. We'll refer them via these navigation targets:

```kotlin

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

Next, let's modify `RootNode` so it extends `ParentNode`:

```kotlin
class RootNode(
    buildContext: BuildContext
) : ParentNode<NavTarget>(
    navModel = TODO("We will come back to this in Step 4"),
    buildContext = buildContext
) {
```

`ParentNode` expects us to implement the abstract method `resolve`. This is how we relate navigation targets to actual children. Let's use these helper methods to define some placeholders for the time being – we'll soon make them more appealing:

```kotlin
override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
    when (navTarget) {
        NavTarget.Child1 -> node(buildContext) { Text(text = "Placeholder for child 1") }
        NavTarget.Child2 -> node(buildContext) { Text(text = "Placeholder for child 2") } 
        NavTarget.Child3 -> node(buildContext) { Text(text = "Placeholder for child 3") }
    }
```

Great! With this mapping created, we can now just refer to children using the sealed class elements, and Appyx will be able to relate them to other nodes.

## 4. Add a back stack

The project wouldn't compile just yet. `ParentNode` expects us to pass an instance of an `AppyxComponent` – the main control structure in any case when we want to add children. No need to worry now – for simplicity, let's just go with a simple `BackStack` implementation here:

```kotlin
class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTarget = NavTarget.Child1,
            savedStateMap = buildContext.savedStateMap,        
        ),
        motionController = { BackStackFader(it) }
    )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = backStack // pass it here
) {
```

With this simple addition we've immediately gained a lot of power! Now we can use the back stack's API to add, replace, pop children with operations like:

```kotlin
backStack.push(NavTarget.Child2)    // will add a new navigation target to the end of the stack and make it active 
backStack.replace(NavTarget.Child3) // will replace the currently active child
backStack.pop()                     // will remove the currently active child and restore the one before it
```

Since we passed the back stack to the `ParentNode`, all such changes will be immediately reflected. We only need to add it to the composition:

```kotlin
@Composable
override fun View(modifier: Modifier) {
    Column {
        Text("Hello world!")
        
        // Let's add the children to the composition
        AppyxComponent(
            appyxComponent = backStack
        )
        
        // Let's also add some controls so we can test it
        Row {
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

## 5. Transitions

Adding sliding transitions instead of the default cross-fade is as simple as changing this:

```kotlin
motionController = { BackStackFader(it) }
```

to this:

```kotlin
motionController = { BackStackSlider(it) }
```

Need something more custom?

1. You can check out some other visualisations in the [Back stack documentation](../components/backstack.md), or [create your own](../interactions/ui-representation.md).
2. Instead of a back stack, you can also find other [Components](../components/index.md) in the library, or you can [create your own](../interactions/appyxcomponent.md).


## 6. Proper child nodes  

As a last step, let's replace at least one of the child placeholders with another proper node.

Let's create a dedicated class:

```kotlin
class SomeChildNode(
    buildContext: BuildContext
) : Node(
    buildContext = buildContext
) {
    @Composable
    override fun View(modifier: Modifier) {
        Text("This is SomeChildNode")
    }
}
```

Now we can update the `resolve` method in `RootNode` so that the target `Child3` refers to this node. It should work out of the box:

```kotlin
override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
    when (navTarget) {
        NavTarget.Child1 -> node(buildContext) { Text(text = "Placeholder for child 1") }
        NavTarget.Child2 -> node(buildContext) { Text(text = "Placeholder for child 2") } 
        NavTarget.Child3 -> SomeChildNode(buildContext)
    }
```

## What's next?

Congrats, you've just built your first Appyx tree!

You can repeat the same pattern and make any embedded children also a `ParentNode` with their own children, navigation models, and transitions. As complexity grows, generally you would:

1. Have a `Node`
2. At some point make it a `ParentNode` and add children to it
3. At some point extract the increasing complexity from a placeholder to another `Node` 
4. Repeat the same on children, go to `1.`


## Further reading

- Check out [Model-driven navigation](concepts/model-driven-navigation.md) how to take your navigation to the next level
- You can (and probably should) also extract local business logic, the view, any any other components into separate classes and [Plugins](features/plugins.md).
