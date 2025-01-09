

# Transitions

You can have arbitrary visualisations and transitions for any [NavModel](../navmodel/index.md). For example, all of these are different representations of the same [Back stack](../navmodel/backstack.md):

<img src="https://i.imgur.com/8gy3Ghb.gif" width="100">
<img src="https://miro.medium.com/max/540/1*tMV-19-CCAMW04bpk9l2Mw.gif" width="100">
<img src="https://camo.githubusercontent.com/aa0c9accaaf6aadc2ab0cfac4c43b194e31a6571f90d381ee7f7fd7f6acc8bcd/68747470733a2f2f692e696d6775722e636f6d2f777844716747652e676966" width="100">
<img src="https://camo.githubusercontent.com/067dc79e29d889b70d3a2f6f0b7bdc42ab268352387f02d77e87e0f0aab4bb52/68747470733a2f2f692e696d6775722e636f6d2f50394e4275696a2e676966" width="100">

Below you can find the different options how to visualise `NavModel` state changes.


## No transitions

Using the provided [Child-related composables](children-view.md) you'll see no transitions as a default – UI changes resulting from the NavModel's state update will be rendered instantly.


## Shared element transitions

To support shared element transition between two Child Nodes you need:

1. Use the `sharedElement` Modifier with the same key on the composable you want to connect.
2. On the `Children` composable, set `withSharedElementTransition` to true and use either fader or
   no transition handler at all. Using a slider will make the shared element slide away with the
   rest of of the content.
3. When operation is performed on the NavModel, the shared element will be animated between the two
   Child Nodes. For instance, in the example below backStack currently has NavTarget.Child1 as the
   active element. Performing a push operation with NavTarget.Child2 will animate the shared element
   between NodeOne and NodeTwo. Popping back to NavTarget.Child1 will animate the shared element back.

```kotlin
class NodeOne(
   buildContext: BuildContext
) : Node(
   buildContext = buildContext
) {

   @Composable
   override fun View(modifier: Modifier) {
      Box(
         modifier = Modifier
            // make sure you specify the size before using sharedElement modifier
            .fillMaxSize()
            .sharedElement(key = "sharedContainer")
      ) { /** ... */ }
   }
}

class NodeTwo(
   buildContext: BuildContext
) : Node(
   buildContext = buildContext
) {
    
   @Composable
   override fun View(modifier: Modifier) {
      Box(
         modifier = Modifier
             // make sure you specify the size before using sharedElement modifier
            .requiredSize(64.dp)
            .sharedElement(key = "sharedContainer")
      ) { /** ... */ }
   }
}

class ParentNode(
   buildContext: BuildContext,
   backStack: BackStack<NavTarget> = BackStack(
      initialElement = NavTarget.Child1,
      savedStateMap = buildContext.savedStateMap
   )
) : ParentNode<NavTarget>(
   buildContext = buildContext,
   navModel = backStack,
) {
    
   override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
      when (navTarget) {
         NavTarget.Child1 -> NodeOne(buildContext)
         NavTarget.Child2 -> NodeTwo(buildContext)
      }
   
   @Composable
   override fun View(modifier: Modifier) {
      Children(
         // or any other NavModel
         navModel = backStack,
         // or no transitionHandler at all. Using a slider will make the shared element slide away
         // with the rest of of the content.
         transitionHandler = rememberBackStackFader(),
         withSharedElementTransition = true
      )
   }
}

```

## Transitions with movable content

You can move composable content between two Child Nodes without losing its state. You can only move
content from a Node that is currently visible and transitioning to invisible state to a Node that
is currently invisible and transitioning to visible state as movable content is intended to be
composed once design and is moved from one part of the composition to another.

To move content between two Child Nodes you need to use `localMovableContentWithTargetVisibility`
composable function with the correct key to retrieve existing content if it exists or put content
for this key if it doesn't exist. In addition to that, on Parent's `Children` composable you need to
set `withMovableContent` to true.

In the example below when a NodeOne is being replaced with NodeTwo in a BackStack or Spotlight NavModel
`CustomMovableContent("movableContentKey")` will be moved from NodeOne to NodeTwo without losing its
state.


```kotlin
@Composable
fun CustomMovableContent(key: Any) {
    localMovableContentWithTargetVisibility(key = key) {
        // implement movable content here
        var counter by remember(pageId) { mutableIntStateOf(0) }
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                counter++
            }
        }
        Text(text = "$counter")
    }?.invoke()
}


class NodeOne(
   buildContext: BuildContext
) : Node(
   buildContext = buildContext
) {
    
   @Composable
   override fun View(modifier: Modifier) {
       CustomMovableContent("movableContentKey")
   }

}

class NodeTwo(
   buildContext: BuildContext
) : Node(
   buildContext = buildContext
) {

   @Composable
   override fun View(modifier: Modifier) {
      CustomMovableContent("movableContentKey")
   }
}

class ParentNode(
   buildContext: BuildContext,
   backStack: BackStack<NavTarget> = BackStack(
      initialElement = NavTarget.Child1,
      savedStateMap = buildContext.savedStateMap
   )
) : ParentNode<NavTarget>(
   buildContext = buildContext,
   navModel = backStack,
) {

   override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
      when (navTarget) {
         NavTarget.Child1 -> NodeOne(buildContext)
         NavTarget.Child2 -> NodeTwo(buildContext)
      }

   @Composable
   override fun View(modifier: Modifier) {
      Children(
         // or any other NavModel
         navModel = backStack,
         // or no transitionHandler at all. Using a slider will make the shared element slide away
         // with the rest of of the content.
         transitionHandler = rememberBackStackFader(),
         withMovableContent = true,
         withSharedElementTransition = true
      )
   }
}

```

## Jetpack Compose default animations

You can use [standard Compose animations](https://developer.android.com/jetpack/compose/animation) for embedded child `Nodes` in the view, e.g. `AnimatedVisibility`:

```kotlin
var visibility by remember { mutableStateOf(true) }

Child(navElement) { child, _ ->
    AnimatedVisibility(visible = visibility) {
        child()
    }
}
```

## Appyx transition handlers

All the [child composables](children-view.md) provided by Appyx accept an optional `transitionHandler` argument too:

- You can use the provided ones as they're a one-liner to add – you can check the individual [NavModels](../navmodel/index.md) for the ones they come shipped with.
- You can also implement your own.

The benefit of using transition handlers is you can represent any custom state of elements defined by your NavModel with Compose `Modifiers`.

The example below is taken from [custom navigation models](../navmodel/custom.md). It matches custom transition states to different scaling values, and returns a `scale` `Modifier`.

```kotlin
class FooTransitionHandler<T>(
    private val transitionSpec: TransitionSpec<Foo.State, Float> = { spring() }
) : ModifierTransitionHandler<T, Foo.State>() {

    // TODO define a Modifier depending on the state.
    //  Here we'll just mutate scaling: 
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Foo.State>,
        descriptor: TransitionDescriptor<T, Foo.State>
    ): Modifier = modifier.composed {
        val scale = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Foo.State.CREATED -> 0f
                    Foo.State.FOO -> 0.33f
                    Foo.State.BAR -> 0.66f
                    Foo.State.BAZ -> 1.0f
                    Foo.State.DESTROYED -> 0f
                }
            })

        scale(scale.value)
    }
}

// TODO remember to add:
@Composable
fun <T> rememberFooTransitionHandler(
    transitionSpec: TransitionSpec<Foo.State, Float> = { spring() }
): ModifierTransitionHandler<T, Foo.State> = remember {
    FooTransitionHandler(transitionSpec)
}
```

## More info

1. You can find more complex examples in the implementations of other NavModels, such as the [Promoter carousel](../navmodel/promoter.md)
2. You can find [Codelabs tutorials](../how-to-use-appyx/codelabs.md) that help you master custom transitions
3. You can find [Coding challenges](../how-to-use-appyx/coding-challenges.md) related to custom transitions 