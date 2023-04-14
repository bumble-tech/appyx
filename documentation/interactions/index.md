# Appyx Interactions

A state-driven motion kit for Compose Multiplatform.


## State-driven motion

{==

Appyx has a state-driven approach to ui and motion. State is the single source of truth: the UI is not mutated directly, but only as a result of the state changing.

Transitions and gestures act as operations directly on the state itself – the UI only follows.

==}

In this sample, an element is transitioned between 4 possible UI stateS:

```kotlin
// [embed] (possibly a strong spring, to not deal with keyframe/immediate yet)
```

It’s backed by a model which can be in 4 possible states:

```kotlin
enum class ElementState {
  A, B, C, D
}
```

And the corresponding UI states:

```kotlin
// Top-left corner, red
private val uiStateA = TargetUiState(
  position = Position.Target(DpOffset(0.dp, 0.dp)),
  backgroundColor = BackgroundColor.Target(md_red_500),
)

// Top-right corner, green
private val uiStateB = TargetUiState(
  position = Position.Target(DpOffset(200.dp, 0.dp)),
  backgroundColor = BackgroundColor.Target(md_light_green_500)
)

// Bottom-right corner, yellow
private val uiStateC = TargetUiState(
  position = Position.Target(DpOffset(200.dp, 300.dp)),
  backgroundColor = BackgroundColor.Target(md_yellow_500)
)

// Bottom-left corner, blue
private val uiStateD = TargetUiState(
  position = Position.Target(DpOffset(0.dp, 300.dp)),
  backgroundColor = BackgroundColor.Target(md_light_blue_500)
)
```

Let's now add an additional UI property for rotation! 

```kotlin
// Top-left corner, red + no rotation
private val uiStateA = TargetUiState(
  /* ... */
  rotationZ = RotationZ(0f)
)

// Top-right corner, green + a full rotation
private val uiStateB = TargetUiState(
  /* ... */
  rotationZ = RotationZ(360f)
)

// Bottom-right corner, yellow + half rotation
private val uiStateC = TargetUiState(
  /* ... */
  rotationZ = RotationZ(180f)
)

// Bottom-left corner, blue + 3/4 rotation
private val uiStateD = TargetUiState(
  /* ... */
  rotationZ = RotationZ(270f)
)
```

Adding the new UI properties will result in the following sample with no additional effort:

```kotlin
[embed]
```



## Gestures

{==

State changes in Appyx always result in the same exact transition regardless of whether they’re triggered by a button press (business logic in general) or dragging (UI interaction in general).

==}

**Try it**: both of the above examples can be manipulated by dragging as well!

The second example demonstrates the power of the gesture working on the model rather than the UI. No change is required in the gesture handling – when the state is translated to UI properties, all additional UI parameters will be automatically transitioned along with the gesture.


You can find more information in [Gestures](gestures.md).



## Operation modes

{==

Appyx supports two main operation modes: `Keyframe` and `Immediate`. The main difference is in how they handle interrupts: what should happen when new operations are triggered while a transition is already happening.

==}

You can achieve a very different effect by spamming the buttons a few times:

```kotlin
// [embed] (testdrive?) (two buttons)
```

In `Keyframe` mode the current transition isn’t interrupted, and it will be guaranteed to finish first. New states resulting from the applied operations are enqueued for execution afterwards. The overall execution progress speeds up proportionally to the size of enqueued operations.

In `Immediate` mode the current transition is interrupted and will not be finished. New operations overwrite the current target state, resulting in the UI dynamically changing course always towards the latest one.

(i) A `Keyframe` transition can always be interrupted by an `Immediate` one, switching the mode. The opposite is not true: resuming `Keyframe` mode is only possible after the last `Immediate` mode operation settles on its target UI state.




## Relevant classes

```kotlin
// [diagram of model+ui layer]
```


* TransitionModel – Defines the abstract description of state and its changes
    * Operation
    * ModelState
* MotionController – Translates the model to UI
* InteractionModel – packages the above as a high level component
