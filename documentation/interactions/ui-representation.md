---
title: Appyx Interactions – UI representation
---

# Appyx Interactions – UI representation


## Overview

{==

The UI representation translates the abstract model of the [TransitionModel](transitionmodel.md) to visual properties. 

==}

The big picture is that you will define a set of animatable properties, such as:

```kotlin
@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target,
    val rotationZ: RotationZ.Target,
    val backgroundColor: BackgroundColor.Target,
)
```

Then you will create instances of it to represent some key states an element can be in on the UI:

```kotlin
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

Finally, you will map all elements in your model state to instances of `TargetUiState`. In our example there's only one element:

```kotlin

override fun TestDriveModel.State<InteractionTarget>.toUiTargets(): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
    listOf(
        MatchedTargetUiState(element, elementState.toTargetUiState())
    )

fun ElementState.toTargetUiState(): TargetUiState =
    when (this) {
        A -> uiStateA
        B -> uiStateB
        C -> uiStateC
        D -> uiStateD
    }
```

Doing so, Appyx will animate elements between these `TargetUiStates` as the abstract model changes:

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-interactions/interactions/sample2/web/build/dist/js/productionExecutable",
        compile_task=":demos:mkdocs:appyx-interactions:interactions:sample2:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-interactions-ui-1",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}


## A more detailed view

Classes related to the UI representation:

- `MotionProperty` – defines an animatable UI property
- `MotionProperty.Target` – defines a target value for its own `MotionProperty`
- `TargetUiState` – a collection of different `MotionProperty.Target` values representing an element's desired representation
- `MutableUiState` – mappable from `TargetUiState`, this class holds the transient state of animated values as they change between different targets


## MotionProperty

Appyx comes with a set of classes derived from `MotionProperty`. A non-exhaustive list of them:
 
- `Alpha`
- `BackgroundColor`
- `Position`
- `RotationX` 
- `RotationY` 
- `RotationZ` 
- `Scale`
- `ZIndex`
- etc.

They represent UI properties that Appyx can animate. Their animated values will be automatically mapped to Compose `Modifier` instances.

If you need a property that's not provided by Appyx, it should be very easy to create one (also consider submitting a PR).


## Target values

A `MotionProperty` is mutable towards target values. `Target` specifies such a value. They're defined by each `MotionProperty`, and are supposed to be immutable.

For example `Alpha.Target` expects a `Float`:

```kotlin
class Alpha /*...*/ {

    class Target(
        val value: Float,
        /*...*/
    ) : MotionProperty.Target
}
```

While `Position.Target` deals with `Offset`:

```kotlin
class Position /*...*/ {

    class Target(
        val value: DpOffset,
        /*...*/
    ) : MotionProperty.Target
}
```


## TargetUiState 

Create your `TargetUiState` as the collection of `MotionProperty.Target` properties that you want Appyx to animate. For example, if you only want cross-fade animation, you would only need `Alpha` in your UI. Then you would define your `TargetUiState` such as:

```kotlin
@MutableUiStateSpecs
class TargetUiState(
    val alpha: Alpha.Target,
)
```

Or, as in the `TestDrive` example, position, rotation, and color: 

```kotlin
@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target,
    val rotation: Rotation.Target,
    val backgroundColor: BackgroundColor.Target,
)
```


## MutableUiState

You might have noticed that the above classes are annotated with `@MutableUiStateSpecs`. Appyx comes with a KSP processor that will generate the corresponding `MutableUiState` class for you, so that you don't have to. 

Please refer to the [KSP setup](ksp.md) guide.


## Observing MotionProperty in children UI

Sometimes children UI can depend on the transition animation values. Appyx provides an API to observe `MotionProperty` inside children UI.

In this example, children composable retrieve and display `DpOffset` value from `Position` motion property as well as `Float` value from `RotationY` using this API:

```kotlin
// returns dpOffset value if transition has Position MotionProperty and null otherwise 
val dpOffset : DpOffset? = motionPropertyRenderValue<Position.Value, Position>()?.offset

// returns rotationY value if transition has RotationY MotionProperty and null otherwise 
val rotationY = motionPropertyRenderValue<Float, RotationY>()
```

{{
    compose_mpp_sample(
        project_output_directory="demos/mkdocs/appyx-interactions/interactions/observemp/web/build/dist/js/productionExecutable",
        compile_task=":demos:mkdocs:appyx-interactions:interactions:observemp:web:jsBrowserDistribution",
        width=512,
        height=384,
        target_directory="samples/documentation-interactions-ui-observe-mp",
        html_file_name="index.html",
        classname="compose_mpp_sample",
    )
}}
