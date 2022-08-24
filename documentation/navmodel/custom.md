# Implementing your own routing sources

A step-by-step guide. You can also take a look at other existing examples to see these in practice.

## Step 1

Create the class; define your possible states; define your initial state. 

`<T>` will refer to the `Routing` sealed class of the client code.

```kotlin
class Foo<T : Any>(
    initialItems: List<T> = listOf(),
    savedStateMap: SavedStateMap?
) : BaseNavModel<T, Foo.TransitionState>(
    screenResolver = FooOnScreenResolver, // We'll see about this shortly
    finalState = DESTROYED, // Anything transitioning towards this state will be discarded eventually
    savedStateMap = savedStateMap // It's nullable if you don't need state restoration
) {

    // Your possible states for any single routing
    enum class TransitionState {
        CREATED, FOO, BAR, BAZ, DESTROYED;
    }

    // You can go about it any other way.
    // Back stack for example defines only a single element.
    // Here we take all the <T> elements and make them transition CREATED -> FOO immediately.
    override val initialElements = initialItems.map {
        FooElement(
            key = RoutingKey(it),
            fromState = TransitionState.CREATED,
            targetState = TransitionState.FOO,
            operation = Operation.Noop()
        )
    }
}
```

## (optional) Step 2

Add some convenience aliases:

```kotlin
typealias FooElement<T> = RoutingElement<T, Foo.TransitionState>

typealias FooElements<T> = RoutingElements<T, Foo.TransitionState>

sealed interface FooOperation<T> : Operation<T, Foo.TransitionState>
```


## Step 3

Define one or more operations.

```kotlin
@Parcelize
class SomeOperation<T : Any> : FooOperation<T> {

    override fun isApplicable(elements: FooElements<T>): Boolean =
        TODO("Define whether this operation is applicable given the current state")
    
    override fun invoke(
        elements: FooElements<T>,
    ): RoutingElements<T, Foo.TransitionState> =
        // TODO: Mutate elements however you please. Add, remove, change.
        //  In this example we're changing all elements to transition to BAR.
        elements.map {
            it.transitionTo(
                newTargetState = BAR,
                operation = this
            )
        }
}

// You can add an extension method for a leaner API
fun <T : Any> Foo<T>.someOperation() {
    accept(FooOperation())
}
```

## Step 4

Add the screen resolver to define which states should be / should not be part of the composition in the end:

```kotlin
object FooOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            Foo.TransitionState.CREATED,
            Foo.TransitionState.DESTROYED -> false
            Foo.TransitionState.FOO,
            Foo.TransitionState.BAR,
            Foo.TransitionState.BAZ, -> true
        }
}
```

## Step 5

Add one or more transition handlers to interpret different states and translate them to Jetpack Compose `Modifiers`. 

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


## Test it

Add `Children` to your `Node`. Pass your NavModel and the transition handler:

```kotlin
@Composable
override fun View(modifier: Modifier) {
    Children(
        modifier = Modifier.fillMaxSize(),
        navModel = foo,
        transitionHandler = rememberFooTransitionHandler()
    )
}
```

Somewhere else in your business logic trigger the operations you defined. Make sure they're called on the same `foo` instance that you pass to the `Children` composable:

```kotlin
foo.someOperation()
```

As soon as this is triggered, elements should transition to the `BAR` state in this example, and you should see them scale up defined by the transition handler.


## Created something cool?

Let us know!
