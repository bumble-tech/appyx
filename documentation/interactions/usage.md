---
title: Appyx Interactions – Using components
---

# Appyx Interactions – Using components

## Overview

{==

A component packaged together with Appyx is called an `AppyxComponent`. 

==}

## Using AppyxComponents

If you're solely interested in using already created components, you don't need to learn about the internals of them. This page introduces the minimum information to get them into your Compose-based projects.


## The big picture

``` mermaid
flowchart TB
  O[Operation] --> I[AppyxComponent] --> |Modifier| C(["@Composable"]);
  C --> |Gesture| O
  B([Business logic]) --> O
```

Where:

* `AppyxComponent` – The packaged component; its output will result in `@Composable` elements with animated `Modifiers`
* `Operation` – Allows to change the state of the component. It can be triggered programmatically or by gestures. Both options are specific to the component implementation.


## Instantiating & configuration

This section shows a generic approach that should be applicable to most components. For specific parameters and optional configuration, please refer to the actual component's own API.

```kotlin
@Composable
fun SomeComposable() {
    val appyxComponent = remember {
        SomeAppyxComponent(
            // List of elements, initial state, etc. go in the model:
            model = SomeTransitionModel(/*...*/),
            
            // The visual representation (slider, fader, etc.) 
            motionController = { SomeMotionController(/*...*/) } ,
            
            // Optional – configure animations
            animationSpec = spring(stiffness = Spring.StiffnessLow),

            // What kind of gestures to control this model with 
            gestureFactory = { SomeMotionController.Gestures(/*...*/) },

            // Optional – configure behaviour of incomplete gestures
            gestureSettleConfig = GestureSettleConfig(
                completionThreshold = 0.2f,
                completeGestureSpec = spring(),
                revertGestureSpec = spring(),
            ),
        )
    }
}
```

## Rendering the AppyxComponent

You can render your component with the `AppyxComponent` composable. Make sure to apply `elementUiModel.modifier`  if you override the optional `element` rendering.

```kotlin
@Composable
fun SomeComposable() {
    AppyxComponent(
        appyxComponent = yourComponent,
        clipToBounds = false,
        modifier = Modifier,
        element = { elementUiModel ->
            YourElementComposable(
                elementUiModel = elementUiModel,
                modifier = elementUiModel.modifier
            )
        }
    )
}
```

You can also make use of the `DraggableAppyxComponent` composable, if you have components that allow gestures to control them:

```kotlin
@Composable
fun SomeComposable() {
    DraggableChildren(
        modifier = Modifier,
        screenWidthPx = (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density).roundToInt(),
        screenHeightPx = (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).roundToInt(),
        appyxComponent = yourComponent,
        gestureValidator = permissiveValidator,
    ) { elementUiModel ->
        YourElementComposable(
            elementUiModel = elementUiModel,
            modifier = elementUiModel.modifier
        )
    }
}
```

## Interacting with the AppyxComponent

How you use your model will depend on the specific component. However, typically, you will have a high level API to trigger changes, such as:

```kotlin
{ backStack.pop() }
{ spotlight.next() }
etc.
```

Models should also offer gestures to control them; please refer to their specific API documentation.
