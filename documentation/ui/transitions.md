# Transitions

## No transitions

Using the provided [Child-related composables](children-view.md) you'll see no transitions as a default – UI changes resulting from the NavModel's state update will be rendered instantly. 


## Jetpack Compose default animations

You can use [standard Compose animations](https://developer.android.com/jetpack/compose/animation) for embedded child `Nodes` in the view, e.g. `AnimatedVisibility`:

```kotlin
var visibility by remember { mutableStateOf(true) }

Child(routingElement) { child, _ ->
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

The example below is taken from [custom routing sources](../navmodel/custom.md). It matches custom transition states to different scaling values, and returns a `scale` `Modifier`. 

```kotlin
class FooTransitionHandler<T>(
    private val transitionSpec: TransitionSpec<Foo.TransitionState, Float> = { spring() }
) : ModifierTransitionHandler<T, Foo.TransitionState>() {

    // TODO define a Modifier depending on the state.
    //  Here we'll just mutate scaling: 
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Foo.TransitionState>,
        descriptor: TransitionDescriptor<T, Foo.TransitionState>
    ): Modifier = modifier.composed {
        val scale = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    Foo.TransitionState.CREATED -> 0f
                    Foo.TransitionState.FOO -> 0.33f
                    Foo.TransitionState.BAR -> 0.66f
                    Foo.TransitionState.BAZ -> 1.0f
                    Foo.TransitionState.DESTROYED -> 0f
                }
            })

        scale(scale.value)
    }
}

// TODO remember to add:
@Composable
fun <T> rememberFooTransitionHandler(
    transitionSpec: TransitionSpec<Foo.TransitionState, Float> = { spring() }
): ModifierTransitionHandler<T, Foo.TransitionState> = remember {
    FooTransitionHandler(transitionSpec)
}
```

You can find more complex examples in the implementations of other routing sources, such as the [Promoter carousel](../navmodel/promoter.md) 
