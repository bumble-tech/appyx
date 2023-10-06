---
title: Appyx Interactions – TransitionModel
---

# Appyx Interactions – TransitionModel


## Overview

{==

A `TransitionModel` defines the abstract model of the [AppyxComponent](appyxcomponent.md).

==}

## <ModelState\>

`TransitionModel` holds an instance of `<ModelState>`, a type defined by the implementation class.

You can find various examples in the repository. Some possible points of interest:

- `TestDriveModel.State`
- `BackStackModel.State`
- `SpotlightModel.State`
- `CardsModel.State`


{==

Model state should have no knowledge of UI-related properties – all of that belongs to the UI layer. You should strive to describe your model in a semantic way that assumes nothing of its visual representation.

==}

For example, `TestDriveModel.State` describes an element that can be in 4 possible configurations (the example in this documentation's main page).

Notice how the states, even though simplistic, aren't named `TOPLEFT`, `TOPRIGHT`, `BOTTOMRIGHT`, `BOTTOMLEFT`, as the positioning is only done in the UI. This allows to represent them in various different ways, without any assumptions baked in the model.  

```kotlin
    @Parcelize
    data class State<InteractionTarget>(
        val element: Element<InteractionTarget>,
        val elementState: ElementState
    ) : Parcelable {
        enum class ElementState {
            A, B, C, D;

            fun next(): ElementState =
                when (this) {
                    A -> B
                    B -> C
                    C -> D
                    D -> A
                }
        }
    }
```

Another example from `BackStackModel.State`, with purely semantic naming:

```kotlin
    @Parcelize
    data class State<InteractionTarget>(
        /**
         * Elements that have been created, but not yet moved to an active state
         */
        val created: Elements<InteractionTarget> = listOf(),

        /**
         * The currently active element.
         * There should be only one such element in the stack.
         */
        val active: Element<InteractionTarget>,

        /**
         * Elements stashed in the back stack (history).
         */
        val stashed: Elements<InteractionTarget> = listOf(),

        /**
         * Elements that will be destroyed after reaching this state.
         */
        val destroyed: Elements<InteractionTarget> = listOf(),
    ) : Parcelable
```

## <InteractionTarget\>

This generic type is simply meant for you to identify different child elements in your client code. For example, if you have a tabbed component, and each tab would be identified by a different instance of an `enum` or a `sealed class`, then this `enum` or `sealed class` would be your `<InteractionTarget>`.

Then, depending on your `<ModelState>` and operation implementations, you can affect these targets differently.


## Changing the state

A `TransitionModel` behaves like a state machine. If you want a new `ModelState` to be created, you must pass it an instance of an `Operation`. You can read more about them in [Operations](operations.md). 


## Output

`TransitionModel` has an output flow:

``` kotlin
val output: StateFlow<Output<ModelState>>
```

In most cases don't need to deal with this output directly. The [AppyxComponent](appyxcomponent.md) takes care of that, and channels this flow to the `Visualisation` to translate it to a UI representation. 

