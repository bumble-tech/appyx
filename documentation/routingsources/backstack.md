# Back stack

Implements a simple linear history:

- The last element at the end of the stack is considered "active".
- All other elements are considered stashed.

The back stack can never be empty – it always contains at least one element.

The back stack also supports different back press and operation strategies (see further down below).


## States

```kotlin
enum class TransitionState {
    CREATED, ACTIVE, STASHED_IN_BACK_STACK, DESTROYED,
}
```

## Constructing the back stack

As the back stack can never be empty, it's required to define an initial routing.

```kotlin
class BackStack<Routing : Any>(
    initialElement: Routing,
    savedStateMap: SavedStateMap?,
    // Optional parameters are omitted
)
```

## Default on screen resolution

As a default, only the active element is considered on screen.

```kotlin
object BackStackOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.CREATED,
            TransitionState.STASHED_IN_BACK_STACK,
            TransitionState.DESTROYED -> false
            TransitionState.ACTIVE -> true
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

`backStack.push(routing)`

Effect on stack: 
```
[A, B, C] + Push(D) = [A, B, C, D]
```

Transitions the active element `ACTIVE` -> `STASHED_IN_BACK_STACK`.
Adds a new element at the end of the stack with a `CREATED` -> `ACTIVE` transition.


#### Replace

`backStack.replace(routing)`

Effect on stack: 
```
[A, B, C] + Replace(D) = [A, B, D]
```

Transitions the active element `ACTIVE` -> `DESTROYED`, which will be removed when the transition finishes.
Adds a new element at the end of the stack with a `CREATED` -> `ACTIVE` transition.


#### Pop

`backStack.pop(routing)`

Effect on stack: 
```
[A, B, C] + Pop = [A, B]
```

Transitions the active element `ACTIVE` -> `DESTROYED`, which will be removed when the transition finishes.
Transitions the last stashed element `STASHED_IN_BACK_STACK` -> `ACTIVE`.


#### Single top

`backStack.singleTop(routing)`

Effect on stack: depends on the contents of the stack:

```
[A, B, C, D] + SingleTop(B)  = [A, B]          // of same type and equals, acts as n * Pop
[A, B, C, D] + SingleTop(B') = [A, B']         // of same type but not equals, acts as n * Pop + Replace
[A, B, C, D] + SingleTop(E)  = [A, B, C, D, E] // not found, acts as Push
```


## Back press strategy

You can override the default strategy in the constructor. You're not limited to using the provided classes, feel free to implement your own.

```kotlin
class BackStack<Routing : Any>(
    /* ... */
    backPressHandler: BackPressHandlerStrategy<Routing, TransitionState> = PopBackPressHandler(),
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
class BackStack<Routing : Any>(
    /* ... */
    operationStrategy: OperationStrategy<Routing, TransitionState> = ExecuteImmediately(),    
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
