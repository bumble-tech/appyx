# Appyx


## 2.x vs 1.x project organisation

### Appyx 1.0
Packaged as a single library, implementing Model-driven navigation with transitions together.

### Appyx 2.0
The library is packaged as multiple artifacts.

#### :appyx-interactions

- Gesture-driven, state-based motion kit & transition engine
- Compose multiplatform implementation
- Does not contain Node-related functionality → moved to `:appyx-navigation`
- Does not contain Android-specific functionality → moved to `:appyx-navigation`

#### :appyx-navigation

- Mostly analogous to Appyx 1.0’s functionality but without the transitions
- Cares about the Node structure & Android-related functionality
- Uses `:appyx-interactions` as a dependency to implement navigation using gestures and transitions
- Compose multiplatform implementation (in progress)

   
#### :appyx-components

- Pre-packaged components to use with `:appyx-navigation`
 

## 2.x Migration guide

Equivalents between **1.x** → **2.x**

- `NavModel` -> `AppyxComponent`
- `TransitionHandler` -> `MotionController`


Including a `NavModel` into the composition in **1.x**:

```kotlin
@Composable
override fun View(modifier: Modifier) {
    Children(
        modifier = Modifier,
        navModel = someNavModel,
        transitionHandler = someTransitionHandler
    )
}
```

Including an `AppyxComponent` into the composition in **2.x**:

```kotlin
@Composable
override fun View(modifier: Modifier) {
    AppyxComponent(
        modifier = Modifier,
        appyxComponent = SomeComponent(
            model = SomeTransitionModel(),
            motionController = { SomeMotionController() } ,
    }
}
```
