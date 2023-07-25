# Appyx Interactions – Operations


## Overview

{==

A `TransitionModel` behaves like a state machine. If you want a new `ModelState` to be created, you must pass it an instance of an `Operation`.

==}


## Operation

Operation defines a `(ModelState) -> ModelState` change.


```kotlin
interface Operation<ModelState> {

    val mode: Mode
    
    fun isApplicable(state: ModelState): Boolean
    
    operator fun invoke(state: ModelState): StateTransition<ModelState>
}
```

### Operation.Mode

A mode maybe specified by client code to define how the `TransitionModel` should handle the state change with regards to interrupts. 

```kotlin
interface Operation<ModelState> {

    enum class Mode {
        IMMEDIATE, KEYFRAME
    }
}
```

You can test their effects on the below sample. Try spamming the buttons:

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-interactions/interactions/sample3/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-interactions:interactions:sample3:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-interactions-index-3",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}

In code, both buttons invoke the same operation, only with different modes:

```kotlin
{ testDrive.next(mode = KEYFRAME) }

// vs

{ testDrive.next(mode = IMMEDIATE, animationSpec = spring(
    stiffness = Spring.StiffnessVeryLow,
    dampingRatio = Spring.DampingRatioMediumBouncy
)) }
```

In `KEYFRAME` mode the current transition isn’t interrupted, and it will be guaranteed to finish before any additional transitions. New states are enqueued for execution afterwards. The overall execution progress speeds up proportionally to the size of enqueued operations.

In `IMMEDIATE` mode the current transition is interrupted and will not be finished. New operations overwrite the current target state, resulting in the UI dynamically changing course always towards the latest one.


## Applicability check

```kotlin
fun isApplicable(state: ModelState): Boolean
```

This method is used for checks without actually executing an `Operation`. 


## Invoking the Operation

This method should create a `StateTransition<ModelState>`:

```kotlin
operator fun invoke(state: ModelState): StateTransition<ModelState>
```

Which is defined as:

```kotlin
data class StateTransition<ModelState>(
    val fromState: ModelState,
    val targetState: ModelState
)
```

It is imperative that:

1. `fromState` is derived from the baseline state (the one passed to `invoke`)
2. `fromState` adds any new elements to the state that should be animated in and are not contained in the baseline state
3. `targetState` is derived from `fromState` and never directly from the baseline state

To make it easier, it is recommended to extend `BaseOperation` which helps to enforce the above:

```kotlin
abstract class BaseOperation<ModelState> : Operation<ModelState> {

    final override fun invoke(baseLineState: ModelState): StateTransition<ModelState> {
        val fromState = createFromState(baseLineState)
        val targetState = createTargetState(fromState)

        return StateTransition(
            fromState = fromState,
            targetState = targetState
        )
    }

    /**
     * @return ModelState If the operation adds any new elements to the scene,
     *                    it MUST add them to the state here.
     *                    Otherwise, just return baseLineState unchanged.
     */
    abstract fun createFromState(baseLineState: ModelState): ModelState

    /**
     * @return ModelState Any elements present in this state
     *                    MUST also be present in the fromState already.
     */
    abstract fun createTargetState(fromState: ModelState): ModelState
}
```


## Examples

You can find examples for implementing operations in the repo. Some possible points of interest:

- `TestDrive` / `MoveTo`
- `BackStack` / `Push`, `Pop`, `Replace`
- `Spotlight` / `Next`, `Prev`, `First`, `Last`

Notice how each of them also comes with their convenient extension methods that allow these
operations to be invoked directly on their corresponding `AppyxComponent`, so that client code can
use a simple API:

```kotlin
// Without extension methods
testDrive.accept(MoveTo(A))
backStack.accept(Pop())
spotlight.accept(Last())

// With extension methods
testDrive.moveTo(A)
backStack.pop()
spotlight.last() 
```

