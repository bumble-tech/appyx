# Quick start guide

## 1. Add Appyx to your project


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

Since this is the root of your tree, you'll also need to plug it in to your Activity, so that system events (Android lifecycle, back press, etc.) reach your components in the tree.

```kotlin
class MainActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                NodeHost(integrationPoint = integrationPoint) {
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

First, let's define the possible set of children using a sealed class. We'll refer them via these routings:

```kotlin

// You can create this class inside the body of RootNode
sealed class Routing : Parcelable {
    @Parcelize
    object Child1 : Routing()

    @Parcelize
    object Child2 : Routing()

    @Parcelize
    object Child3 : Routing()
}
```

Next, let's modify `RootNode` so it extends `ParentNode`:

```kotlin
class RootNode(
    buildContext: BuildContext
) : ParentNode<Routing>(
    buildContext = buildContext
) {
```

`ParentNode` expects us to implement the abstract method `resolve`. This is how we relate routings to actual children. Let's use these helper methods to define some placeholders for the time being – we'll soon make them more appealing:

```kotlin
override fun resolve(routing: Routing, buildContext: BuildContext): Node =
    when (routing) {
        Routing.Child1 -> node(buildContext) { Text(text = "Placeholder for child 1") }
        Routing.Child2 -> node(buildContext) { Text(text = "Placeholder for child 2") } 
        Routing.Child3 -> node(buildContext) { Text(text = "Placeholder for child 3") }
    }
```

Great! With this mapping created, we can now just refer to children using the sealed class elements, and Appyx will be able to relate them to other nodes.

## 4. Add a back stack

The project wouldn't compile just yet. `ParentNode` expects us to pass an instance of a `RoutingSource` – the main control structure in any case when we want to add children. No need to worry now – for simplicity, let's just go with a simple `BackStack` implementation here:

```kotlin
class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Routing.Child1,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<Routing>(
    routingSource = backStack, // pass it here
    buildContext = buildContext
) {
```

With this simple addition we've immediately gained a lot of power! Now we can use the back stack's API to add, replace, pop children with operations like:

```kotlin
backStack.push(Routing.Child2)      // will add a new routing to the end of the stack and make it active 
backStack.replace(Routing.Child3)   // will replace the currently active child
backStack.pop()                     // will remove the currently active child and restore the one before it
```

Since we passed the back stack to the `ParentNode`, all such changes will be immediately reflected. We only need to add it to the composition:

```kotlin
@Composable
override fun View(modifier: Modifier) {
    Column {
        Text("Hello world!")
        // Let's add the children to the composition
        Children(
            routingSource = backStack
        )
        
        // Let's also add some controls so we can test it
        Row {
            TextButton(onClick = { backStack.push(Routing.Child1) }) {
                Text(text = "Push child 1")
            }
            TextButton(onClick = { backStack.push(Routing.Child2) }) {
                Text(text = "Push child 2")
            }
            TextButton(onClick = { backStack.push(Routing.Child3) }) {
                Text(text = "Push child 3")
            }
            TextButton(onClick = { backStack.pop() }) {
                Text(text = "Pop")
            }
        }
    }
}
```

## 5. Add transitions

Adding some transitions is a one-liner:

```kotlin
Children(
    routingSource = backStack,
    transitionHandler = rememberBackstackSlider()
)
```

You can also use a fader instead: ```rememberBackstackFader()```, and you can supply a transition spec in both cases: ```rememberBackStackSlider { spring(stiffness = Spring.StiffnessLow) }```

Need something more custom?

1. Instead of a back stack, you can find other control structures in the library, or you can implement your own
2. Instead of the default transition handlers, you can also supply your own

You can also read the [Back stack documentation](../routingsources/backstack.md) for more info.

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

Now we can update the `resolve` method in `RootNode` so that the routing `Child3` refers to this node. It should work out of the box:

```kotlin
override fun resolve(routing: Routing, buildContext: BuildContext): Node =
    when (routing) {
        Routing.Child1 -> node(buildContext) { Text(text = "Placeholder for child 1") }
        Routing.Child2 -> node(buildContext) { Text(text = "Placeholder for child 2") } 
        Routing.Child3 -> SomeNode(buildContext)
    }
```

## What's next?

Congrats, you've just built your first Appyx tree!

You can repeat the same pattern and make any embedded children also a `ParentNode` with their own children, routing sources, and transitions. As complexity grows, generally you would:

1. Have a `Node`
2. At some point make it a `ParentNode` and add children to it
3. At some point extract the increasing complexity from a placeholder to another `Node` 
4. Repeat the same on children, go to `1.`

You can (and probably should) also extract local business logic, the view, any any other components into separate classes and [plugins](../basics/plugins.md).
