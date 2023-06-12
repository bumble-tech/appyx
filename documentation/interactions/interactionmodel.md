# Appyx Interactions – Creating your own components

## Overview

{==

A component packaged together with Appyx is called an `InteractionModel`. It consists of:

- An abstract model representation
- One or more UI representations
- Gesture interpretation (usually – but not necessarily – tied to the UI representation)

==}

This section of the documentation deals with the internals of an `InteractionModel`. If you're solely interested in using already created components, you can simply refer to [Using components](usage.md) instead.

The big picture:

``` mermaid
flowchart TB
  subgraph I[InteractionModel]
    direction TB
    T[TransitionModel] --> |"#lt;ModelState#gt;"| M[MotionController];
  end
  O[Operation] --> I --> |Modifier| C(["@Composable"]);
  C --> |Gesture| O
  B([Business logic]) --> O
```

Where:

* `TransitionModel` – Defines the abstract description of `<ModelState>`, accepts `Operations`
* `MotionController` – Translates `<ModelState>` to UI middle representation, eventually `Modifier`
* `InteractionModel` – Packages the above as a high level component; its output will result in `@Composable` elements with animated `Modifiers`
* `Operation` – Defines a `(ModelState) -> ModelState` change

You can read more about each in the next sections of the documentation.


## Where to start

It's recommended to extend `BaseInteractionModel` for your implementation. You could also take a look at existing samples in the repository and use them as starting points.


