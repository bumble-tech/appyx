{% include-markdown "../deprecation.md" %}

# Implementing your own navigation models

A step-by-step guide. You can also take a look at other existing examples to see these in practice.

## Step 1

Create the class; define your possible states; define your initial state.

```kotlin
class Foo<NavTarget : Any>(
    initialItems: List<NavTarget> = listOf(),
    savedStateMap: SavedStateMap?
) : BaseNavModel<NavTarget, Foo.State>(
    screenResolver = FooOnScreenResolver, // We'll see about this shortly
    finalState = DESTROYED, // Anything transitioning towards this state will be discarded eventually
    savedStateMap = savedStateMap // It's nullable if you don't need state restoration
) {

    // Your possible states for any single navigation target
    enum class State {
        CREATED, FOO, BAR, BAZ, DESTROYED;
    }

    // You can go about it any other way.
    // Back stack for example defines only a single element.
    // Here we take all the <NavTarget> elements and make them transition CREATED -> FOO immediately.
    override val initialElements = initialItems.map {
        FooElement(
            key = NavKey(it),
            fromState = State.CREATED,
            targetState = State.FOO,
            operation = Operation.Noop()
        )
    }
}
```

## (optional) Step 2

Add some convenience aliases:

```kotlin
typealias FooElement<NavTarget> = NavElement<NavTarget, Foo.State>

typealias FooElements<NavTarget> = NavElements<NavTarget, Foo.State>

sealed interface FooOperation<NavTarget> : Operation<NavTarget, Foo.State>
```

## Step 3

Define one or more operations.

```kotlin
@Parcelize
class SomeOperation<NavTarget : Any> : FooOperation<NavTarget> {

    override fun isApplicable(elements: FooElements<NavTarget>): Boolean =
        TODO("Define whether this operation is applicable given the current state")
    
    override fun invoke(
        elements: FooElements<NavTarget>,
    ): NavElements<NavTarget, Foo.State> =
        // TODO: Mutate elements however you please. Add, remove, change.
        //  In this example we're changing all elements to transition to BAR.
        //  You can also use helper methods elements.transitionTo & elements.transitionToIndexed 
        elements.map {
            it.transitionTo(
                newTargetState = BAR,
                operation = this
            )
        }
}

// You can add an extension method for a leaner API
fun <NavTarget : Any> Foo<NavTarget>.someOperation() {
    accept(FooOperation())
}
```

## Step 4

Add the screen resolver to define which states should be / should not be part of the composition in the end:

```kotlin
object FooOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            Foo.State.CREATED,
            Foo.State.DESTROYED -> false
            Foo.State.FOO,
            Foo.State.BAR,
            Foo.State.BAZ, -> true
        }
}
```

## Step 5

Add one or more transition handlers to interpret different states and translate them to Jetpack Compose `Modifiers`.

```kotlin
class FooTransitionHandler<NavTarget>(
    private val transitionSpec: TransitionSpec<Foo.State, Float> = { spring() }
) : ModifierTransitionHandler<NavTarget, Foo.State>() {

    // TODO define a Modifier depending on the state.
    //  Here we'll just mutate scaling: 
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<Foo.State>,
        descriptor: TransitionDescriptor<NavTarget, Foo.State>
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
fun <NavTarget> rememberFooTransitionHandler(
    transitionSpec: TransitionSpec<Foo.State, Float> = { spring() }
): ModifierTransitionHandler<NavTarget, Foo.State> = remember {
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
