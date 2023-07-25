{% include-markdown "../deprecation.md" %}

# Back stack

<img src="https://i.imgur.com/8gy3Ghb.gif" width="200">

Implements a simple linear history:

- The last element at the end of the stack is considered "active".
- All other elements are considered stashed.
- Children associated with stashed elements are off the screen but kept alive (see how the counter
  values reflect this on the video)

The back stack can never be empty – it always contains at least one element.

The back stack also supports different back press and operation strategies (see further down below).

## States

```kotlin
enum class State {
    CREATED, ACTIVE, STASHED, DESTROYED,
}
```

## Visualisation of states

<img src="https://camo.githubusercontent.com/aa0c9accaaf6aadc2ab0cfac4c43b194e31a6571f90d381ee7f7fd7f6acc8bcd/68747470733a2f2f692e696d6775722e636f6d2f777844716747652e676966" width="200">

Check out the apps in our [Coding challenges](../how-to-use-appyx/coding-challenges.md) – they have an embedded visualisation of what happens to all the elements inside the back stack (look at the row of orange boxes below the logo).

## Constructing the back stack

As the back stack can never be empty, it's required to define an initial target.

```kotlin
class BackStack<NavTarget : Any>(
    initialElement: NavTarget,
    savedStateMap: SavedStateMap?,
    // Optional parameters are omitted
)
```

## Default on screen resolution

As a default, only the active element is considered on screen.

```kotlin
object BackStackOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            State.CREATED,
            State.STASHED,
            State.DESTROYED -> false
            State.ACTIVE -> true
        }
}
```

## Default transition handlers

#### BackStackFader

`rememberBackstackFader()`

Adds simple cross-fading transitions

#### BackStackSlider

`rememberBackstackSlider()`

Adds horizontal sliding transitions so that the `ACTIVE` element is in the center; other states are animated from / to the left or the right edge of the screen.

## Operations

#### Push

`backStack.push(navTarget)`

Effect on stack: 
```
[A, B, C] + Push(D) = [A, B, C, D]
```

Transitions the active element `ACTIVE` -> `STASHED`.
Adds a new element at the end of the stack with a `CREATED` -> `ACTIVE` transition.

#### Replace

`backStack.replace(navTarget)`

Effect on stack: 
```
[A, B, C] + Replace(D) = [A, B, D]
```

Transitions the active element `ACTIVE` -> `DESTROYED`, which will be removed when the transition finishes.
Adds a new element at the end of the stack with a `CREATED` -> `ACTIVE` transition.

#### Pop

`backStack.pop(navTarget)`

Effect on stack: 
```
[A, B, C] + Pop = [A, B]
```

Transitions the active element `ACTIVE` -> `DESTROYED`, which will be removed when the transition finishes.
Transitions the last stashed element `STASHED` -> `ACTIVE`.

#### Single top

`backStack.singleTop(navTarget)`

Effect on stack: depends on the contents of the stack:

```
[A, B, C, D] + SingleTop(B)  = [A, B]          // of same type and equals, acts as n * Pop
[A, B, C, D] + SingleTop(B') = [A, B']         // of same type but not equals, acts as n * Pop + Replace
[A, B, C, D] + SingleTop(E)  = [A, B, C, D, E] // not found, acts as Push
```

## Back press strategy

You can override the default strategy in the constructor. You're not limited to using the provided classes, feel free to implement your own.

```kotlin
class BackStack<NavTarget : Any>(
    /* ... */
    backPressHandler: BackPressHandlerStrategy<NavTarget, State> = PopBackPressHandler(),
    /* ... */
) 
```

#### PopBackPressHandler

The default back press handling strategy. Runs a `Pop` operation.

#### DontHandleBackPress

Serves as a no-op.

## Operation strategy

You can override the default strategy in the constructor. You're not limited to using the provided classes, feel free to implement your own.

```kotlin
class BackStack<NavTarget : Any>(
    /* ... */
    operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),    
    /* ... */
)
```

#### ExecuteImmediately
The default strategy. New operations are executed without any questions, regardless of any already running transitions.

#### FinishTransitionsOnNewOperation
All running transitions are abruptly finished when a new one is started

#### QueueOperations
The new operation is queued and executed after the current one finishes

#### IgnoreIfThereAreUnfinishedTransitions
Runs the new one only if there are no transitions happening currently; ignore and discard it otherwise
