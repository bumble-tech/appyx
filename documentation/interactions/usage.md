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
            visualisation = { SomeVisualisation(/*...*/) } ,
            
            // Optional – configure animations
            animationSpec = spring(stiffness = Spring.StiffnessLow),

            // What kind of gestures to control this model with 
            gestureFactory = { SomeVisualisation.Gestures(/*...*/) },

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

### In the scope of Appyx Interactions 

You can render your component with the `AppyxInteractionsContainer` composable. 

Make sure to:

- Apply `elementUiModel.modifier` if you override the optional `element` rendering.
- Provide `screenWidthPx` and `screenWidthPx`

```kotlin
@Composable
fun SomeComposable() {
    AppyxInteractionsContainer(
        appyxComponent = yourComponent,
        screenWidthPx = TODO(),
        screenHeightPx = TODO(),
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

### In the scope of Appyx Navigation

Appyx Navigation extends on the functionality of `AppyxInteractionsContainer` and adds `AppyxNavigationContainer` as a wrapper around it. 

For client code usage they're almost identical. However, you should always use the latter when using Appyx Navigation as it makes sure the related child `Nodes` are lifecycled properly.

Also note:

- This composable is only accessible inside of a `Node`.
- You should use it inside the `View` composable.
- You don't need to specify screen dimensions.


```kotlin
class YourNode(
    /*...*/
) : Node<T> {

    @Composable
    override fun Content(modifier: Modifier) {
        AppyxNavigationContainer(
            appyxComponent = yourComponent,
            modifier = modifier
        )   
    }
}
```

### When to use which?

You should use `AppyxInteractionsContainer` if you're adding standalone Appyx components to your project without using navigation.

You should always use `AppyxNavigationContainer` if you're using Appyx Navigation.



## Interacting with the AppyxComponent

How you use your model will depend on the specific component. However, typically, you will have a high level API to trigger changes, such as:

```kotlin
{ backStack.pop() }
{ spotlight.next() }
etc.
```

Models should also offer gestures to control them; please refer to their specific API documentation.
