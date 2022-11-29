# Transitions
 
You can have arbitrary visualisations and transitions for any [NavModel](../navmodel/index.md). For example, all of these are different representations of the same [Back stack](../navmodel/backstack.md):

<img src="https://i.imgur.com/8gy3Ghb.gif" width="100">
<img src="https://miro.medium.com/max/540/1*tMV-19-CCAMW04bpk9l2Mw.gif" width="100">
<img src="https://camo.githubusercontent.com/aa0c9accaaf6aadc2ab0cfac4c43b194e31a6571f90d381ee7f7fd7f6acc8bcd/68747470733a2f2f692e696d6775722e636f6d2f777844716747652e676966" width="100">
<img src="https://camo.githubusercontent.com/067dc79e29d889b70d3a2f6f0b7bdc42ab268352387f02d77e87e0f0aab4bb52/68747470733a2f2f692e696d6775722e636f6d2f50394e4275696a2e676966" width="100">

Below you can find the different options how to visualise `NavModel` state changes. 


## No transitions

Using the provided [Child-related composables](children-view.md) you'll see no transitions as a default – UI changes resulting from the NavModel's state update will be rendered instantly. 


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
