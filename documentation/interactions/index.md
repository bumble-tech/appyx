# Appyx Interactions – Overview

Component kit with gesture control for Compose Multiplatform.


## State-driven motion

Try this interactive sample! You can either:

- press the `Next` button, or
- 👆 drag the element to move it to its next state.

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-interactions/interactions/sample1/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-interactions:interactions:sample1:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-interactions-index-1",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}


How does this work?

{==

Appyx has a state-driven approach to UI and motion:

- The Model & its UI representation are separated.
- The Model is an abstract representation that knows nothing of the UI.
- The UI is a function of the Model, and is not mutated directly.
- The Model is the single source of truth. To change the UI, we change the Model.
- All transitions and even gestures are implemented as operations acting on the Model directly.

==}


Let's see some of the backing code for the above example. Our model here can be in 4 possible states:

```kotlin
/* Code is shortened for demonstration purposes */
class TestDriveModel {

    @Parcelize
    data class State(
        val elementState: ElementState
    ) : Parcelable {
        
        enum class ElementState {
            A, B, C, D;
        }
    }
}
```

This model is then mapped to the corresponding UI states:

```kotlin
@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target,
    val backgroundColor: BackgroundColor.Target,
)

private val topLeftCorner = TargetUiState(
    position = Position.Target(Alignment.TopStart),
    backgroundColor = BackgroundColor.Target(Color(0xFFFFC629))
)

private val topRightCorner = TargetUiState(
    position = Position.Target(Alignment.TopEnd),
    backgroundColor = BackgroundColor.Target(Color(0xFF353535))
)

private val bottomRightCorner = TargetUiState(
    position = Position.Target(Alignment.BottomEnd),
    backgroundColor = BackgroundColor.Target(Color(0xFFFE9763))
)

private val bottomLeftCorner = TargetUiState(
    position = Position.Target(Alignment.BottomStart),
    backgroundColor = BackgroundColor.Target(Color(0xFF855353))
)

fun ElementState.toTargetUiState(): TargetUiState =
    when (this) {
        A -> topLeftCorner
        B -> topRightCorner
        C -> bottomRightCorner
        D -> bottomLeftCorner
    }
```

## Playing with the UI representation

Let's spice up the UI representation a bit! Let's see what happens if we also associate rotation-related values with the target UI states:

```kotlin
@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target,
    val rotationZ: RotationZ.Target, // <-- +Rotation
    val backgroundColor: BackgroundColor.Target,
)

private val topLeftCorner = TargetUiState(
    position = Position.Target(Alignment.TopStart),
    rotationZ = RotationZ.Target(0f), // <-- +Rotation
    backgroundColor = BackgroundColor.Target(Color(0xFFFFC629))
)

private val topRightCorner = TargetUiState(
    position = Position.Target(Alignment.TopEnd),
    rotationZ = RotationZ.Target(180f), // <-- +Rotation
    backgroundColor = BackgroundColor.Target(Color(0xFF353535))
)

private val bottomRightCorner = TargetUiState(
    position = Position.Target(Alignment.BottomEnd),
    rotationZ = RotationZ.Target(270f), // <-- +Rotation
    backgroundColor = BackgroundColor.Target(Color(0xFFFE9763))
)

private val bottomLeftCorner = TargetUiState(
    position = Position.Target(Alignment.BottomStart),
    rotationZ = RotationZ.Target(540f), // <-- +Rotation
    backgroundColor = BackgroundColor.Target(Color(0xFF855353))
)
```

Adding this new UI property will result in the below sample **with no additional effort**. Notice how you can still drag the element to its next state, and now the rotation is also animated automatically:


{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-interactions/interactions/sample2/web/build/distributions",
        compile_task=":demos:mkdocs:appyx-interactions:interactions:sample2:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-interactions-index-2",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}


## Gestures

{==

Regardless of whether a specific state change is triggered by a button press (business logic in general) or gestures (UI interaction in general), in Appyx both cases result in the same exact visual outcome.

==}

If you haven't tried it yet, check how both of the above examples can be manipulated by dragging as well!

The second example demonstrates the power of Appyx's approach to gesture handling: the gesture changes the model, rather than the UI. When the state is translated to UI properties any and all additional UI parameters we added (rotation in this case) are automatically transitioned along with the gesture – no change is required in the gesture handling in client code.

You can find more information in [Gestures](gestures.md).



## Operation modes

{==

Appyx supports two main operation modes: `Keyframe` and `Immediate`. The main difference is in how they handle interrupts: what should happen when new operations are triggered while a transition is already happening.

==}

You can achieve a very different effect by spamming the buttons a few times:
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

In `KEYFRAME` mode the current transition isn’t interrupted, and it will be guaranteed to finish before any additional transitions. New states are enqueued for execution afterwards. The overall execution progress speeds up proportionally to the size of enqueued operations.

In `IMMEDIATE` mode the current transition is interrupted and will not be finished. New operations overwrite the current target state, resulting in the UI dynamically changing course always towards the latest one.



## What to use Appyx interactions for

**Generally speaking**:

1. You can create just about any component that you can describe with abstract states
2. Which then you can dress up with various UI representations
3. And manipulate with gestures

Check out some our packaged examples in [Appyx components](../components/index.md)!


